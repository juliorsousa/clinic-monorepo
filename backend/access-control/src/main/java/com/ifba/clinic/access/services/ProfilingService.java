package com.ifba.clinic.access.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifba.clinic.access.entities.ProfileIntent;
import com.ifba.clinic.access.entities.User;
import com.ifba.clinic.access.entities.enums.EnumIntentStatus;
import com.ifba.clinic.access.entities.enums.EnumRole;
import com.ifba.clinic.access.exceptions.BadRequestException;
import com.ifba.clinic.access.exceptions.ConflictException;
import com.ifba.clinic.access.exceptions.NoContentException;
import com.ifba.clinic.access.messaging.intents.producers.ProfileIntentProducer;
import com.ifba.clinic.access.models.requests.profiles.ProfileIntentRequest;
import com.ifba.clinic.access.models.response.ProfileIntentProcessingResponse;
import com.ifba.clinic.access.models.response.ProfileIntentResponse;
import com.ifba.clinic.access.repositories.ProfileIntentRepository;
import com.ifba.clinic.access.security.annotations.AuthRequired;
import com.ifba.clinic.access.security.services.AuthenticationService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import static com.ifba.clinic.access.utils.Messages.GENERIC_BAD_REQUEST;
import static com.ifba.clinic.access.utils.Messages.GENERIC_NO_CONTENT;
import static com.ifba.clinic.access.utils.Messages.INTENT_ALREADY_EXISTS;
import static com.ifba.clinic.access.utils.Messages.ROLE_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilingService {

  private final AuthenticationService authenticationService;
  private final ProfileIntentRepository profileIntentRepository;
  private final UserService userService;

  private final ProfileIntentProducer profileIntentProducer;

  private final ObjectMapper objectMapper;

  private static String LOG_USER_ALREADY_HAS_ROLE =
      "User with email: {} already has any role, skipping trait updates.";

  @Transactional
  @AuthRequired
  public ProfileIntentResponse getCurrentProfileIntent() {
    User currentUser = userService.getCurrentUser();

    var results = profileIntentRepository
        .findCurrentByUser(currentUser, false, PageRequest.of(0, 1));

    log.info("Fetching current profile intent for user with email: {}", currentUser.getEmail());

    Optional<ProfileIntent> intentOptional = results.stream().findFirst();

    if (intentOptional.isPresent()) {
      ProfileIntent intent = intentOptional.get();

      log.info("Current profile intent found with ID: {}", intent.getId());

      return ProfileIntentResponse.builder()
          .id(intent.getId())
          .status(intent.getStatus())
          .type(intent.getType())
          .response(intent.getStatus() == EnumIntentStatus.ERRORED ? intent.getResponse() : null)
          .build();
    }

    throw new NoContentException(GENERIC_NO_CONTENT);
  }

  @Transactional(value = Transactional.TxType.REQUIRES_NEW)
  @AuthRequired
  public ProfileIntentResponse createProfileIntent(ProfileIntentRequest request) {
    User currentUser = userService.getCurrentUser();
    EnumRole requestedRole = request.profile().toEnum();

    log.info("Creating profile setup intent for user with email: {}", currentUser.getEmail());

    checkExistingIntent(currentUser, requestedRole);
    checkExistingRole(currentUser, requestedRole);

    if (requestedRole == EnumRole.PATIENT) {
      String body;

      try {
        body = objectMapper.writeValueAsString(request);
      } catch (JsonProcessingException e) {
        log.error("Error serializing patient ProfileIntentRequest: {}", e.getMessage());

        throw new BadRequestException(GENERIC_BAD_REQUEST);
      }

      ProfileIntent intent = ProfileIntent.builder()
          .user(currentUser)
          .type(requestedRole)
          .status(EnumIntentStatus.IMPLICIT)
          .body(body)
          .build();

//      deleteOldErroredIntents(currentUser, requestedRole);

      ProfileIntent saved = profileIntentRepository.save(intent);

      log.info("Profile intent created with ID: {}", saved.getId());

      if (alreadyHasAnyRole(currentUser)) {
        log.info(LOG_USER_ALREADY_HAS_ROLE, currentUser.getEmail());
      } else {
        userService.removeTraitsFromUser(currentUser.getId(), List.of("PENDING_ONBOARDING"));
        userService.addTraitToUser(currentUser.getId(), "AWAITING_PROFILE_CREATION");
      }

      try {
        profileIntentProducer.sendRunProfileIntent(saved);
      } catch (JsonProcessingException e) {
        log.error("Failed to send profile intent to messaging queue: {}", e.getMessage());

        throw new RuntimeException("Failed to process profile intent.", e);
      }

      return ProfileIntentResponse.builder()
          .id(saved.getId())
          .status(saved.getStatus())
          .type(saved.getType())
          .build();
    }

    if (requestedRole == EnumRole.DOCTOR) {
      String body;

      try {
        body = objectMapper.writeValueAsString(request);
      } catch (JsonProcessingException e) {
        log.error("Error serializing doctor ProfileIntentRequest: {}", e.getMessage());

        throw new BadRequestException(GENERIC_BAD_REQUEST);
      }

      ProfileIntent intent = ProfileIntent.builder()
          .user(currentUser)
          .type(requestedRole)
          .status(EnumIntentStatus.PENDING)
          .body(body)
          .build();

//      deleteOldErroredIntents(currentUser, requestedRole);

      ProfileIntent saved = profileIntentRepository.save(intent);

      log.info("Profile intent created with ID: {}", saved.getId());

      if (alreadyHasAnyRole(currentUser)) {
        log.info(LOG_USER_ALREADY_HAS_ROLE, currentUser.getEmail());
      } else {
        userService.removeTraitsFromUser(currentUser.getId(), List.of("PENDING_ONBOARDING"));
        userService.addTraitToUser(currentUser.getId(), "AWAITING_INTENT_APPROVAL");
      }

      return ProfileIntentResponse.builder()
          .id(saved.getId())
          .status(saved.getStatus())
          .type(saved.getType())
          .build();
    }

    throw new BadRequestException("Only patient and doctor profile intents can be created at this time.");
  }

//  @Transactional
//  public void deleteOldErroredIntents(User user, EnumRole type) {
//    var oldErroredIntents = profileIntentRepository
//        .findByUserAndTypeAndStatus(user, type, EnumIntentStatus.ERRORED);
//
//    if (oldErroredIntents.isEmpty()) {
//      return;
//    }
//
//    profileIntentRepository.deleteAll(oldErroredIntents);
//
//    log.info("Deleted {} old errored profile intents for user with email: {}", oldErroredIntents.size(), user.getEmail());
//  }

  @Transactional(value = Transactional.TxType.REQUIRES_NEW)
  public void processProfileIntentResponse(ProfileIntentProcessingResponse response) {
    var intentOpt = profileIntentRepository.findById(response.intentId());

    if (intentOpt.isEmpty()) {
      log.error("Profile intent with ID {} not found.", response.intentId());
      return;
    }

    ProfileIntent intent = intentOpt.get();

    intent.setResponse(response.response());
    intent.setStatus(EnumIntentStatus.valueOf(response.status()));

    if (intent.getStatus() == EnumIntentStatus.PROCESSED) {
      userService.addRoleToUser(intent.getUser().getId(), intent.getType(), response.entityId());

      CompletableFuture.runAsync(
          () -> {
            userService.removeTraitsFromUser(intent.getUser().getId(), List.of("AWAITING_PROFILE_CREATION", "AWAITING_INTENT_APPROVAL"));
          },
          CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS)
      );

      log.info("Profile intent with ID {} processed successfully.", intent.getId());
    }

    if (intent.getStatus() == EnumIntentStatus.ERRORED) {
      List<String> mandatoryOnboardingTraits = List.of("AWAITING_PROFILE_CREATION", "AWAITING_INTENT_APPROVAL");

      if (userService.hasAnyTrait(intent.getUser().getId(), mandatoryOnboardingTraits)) {
        CompletableFuture.runAsync(
            () -> {
              userService.removeTraitsFromUser(intent.getUser().getId(), mandatoryOnboardingTraits);
              userService.addTraitToUser(intent.getUser().getId(), "PENDING_ONBOARDING");
            },
            CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS)
        );
      }

      log.info("Profile intent with ID {} errored during processing.", intent.getId());
    }

    profileIntentRepository.save(intent);
  }

  private void checkExistingIntent(User user, EnumRole type) {
    var existingIntent = profileIntentRepository
        .findCurrentByUserAndType(user, type, false, PageRequest.of(0, 1));


    if (!existingIntent.isEmpty()) {
      throw new ConflictException(INTENT_ALREADY_EXISTS);
    }
  }

  private void checkExistingRole(User user, EnumRole type) {
    boolean hasRole = user.getRoles().stream()
        .anyMatch(role -> role.getRole().equals(type));

    if (hasRole) {
      throw new ConflictException(ROLE_ALREADY_EXISTS);
    }
  }

  private boolean alreadyHasAnyRole(User user) {
    return user.getRoles().stream()
        .findAny()
        .isPresent();
  }

}
