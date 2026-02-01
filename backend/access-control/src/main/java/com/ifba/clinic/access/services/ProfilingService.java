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
import com.ifba.clinic.access.models.requests.profiles.ProfileIntentRequest;
import com.ifba.clinic.access.models.response.ProfileIntentProcessingResponse;
import com.ifba.clinic.access.models.response.ProfileIntentResponse;
import com.ifba.clinic.access.messaging.producers.ProfilingIntentProducer;
import com.ifba.clinic.access.repositories.ProfileIntentRepository;
import com.ifba.clinic.access.security.annotations.AuthRequired;
import com.ifba.clinic.access.security.services.AuthenticationService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static com.ifba.clinic.access.utils.Messages.GENERIC_BAD_REQUEST;
import static com.ifba.clinic.access.utils.Messages.GENERIC_NO_CONTENT;
import static com.ifba.clinic.access.utils.Messages.INTENT_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilingService {

  private final AuthenticationService authenticationService;
  private final ProfileIntentRepository profileIntentRepository;
  private final UserService userService;

  private final ProfilingIntentProducer profilingIntentProducer;

  private final ObjectMapper objectMapper;

  @Transactional
  @AuthRequired
  public ProfileIntentResponse getCurrentProfileIntent() {
    User currentUser = userService.getCurrentUser();

    var existingIntent = profileIntentRepository
        .findCurrentByUser(currentUser, true);

    if (existingIntent.isPresent()) {
      ProfileIntent intent = existingIntent.get();

      return ProfileIntentResponse.builder()
          .id(intent.getId())
          .status(intent.getStatus())
          .type(intent.getType())
          .build();
    }

    throw new NoContentException(GENERIC_NO_CONTENT);
  }

  @Transactional
  @AuthRequired
  public ProfileIntentResponse createProfileIntent(ProfileIntentRequest request) {
    // CREATE A DATABASE RECORD FOR PROFILE INTENT AND RETURN A INTENT ID

    // IF PATIENT, SUBMIT DIRECTLY TO PEOPLE-MANAGEMENT SERVICE
    // IF DOCTOR, RETURN INTENT ID PLUS FLAG SAYING ADDITIONAL VERIFICATION NEEDED

    User currentUser = userService.getCurrentUser();
    EnumRole requestedRole = request.profile().toEnum();

    log.info("Creating profile setup intent for user with email: {}", currentUser.getEmail());

    checkExistingIntent(currentUser, requestedRole);

    if (requestedRole == EnumRole.PATIENT) {
      String body;

      try {
        body = objectMapper.writeValueAsString(request);
      } catch (JsonProcessingException e) {
        log.error("Error serializing ProfileIntentRequest: {}", e.getMessage());

        throw new BadRequestException(GENERIC_BAD_REQUEST);
      }

      ProfileIntent intent = ProfileIntent.builder()
          .user(currentUser)
          .type(requestedRole)
          .status(EnumIntentStatus.IMPLICIT)
          .body(body)
          .build();

      ProfileIntent saved = profileIntentRepository.save(intent);

      log.info("Profile intent created with ID: {}", saved.getId());

      userService.removeTraitsFromUser(currentUser.getId(), List.of("PENDING_ONBOARDING"));
      userService.addTraitToUser(currentUser.getId(), "AWAITING_PROFILE_CREATION");

      try {
        profilingIntentProducer.sendRunProfileIntent(saved);
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

    throw new BadRequestException("Only patient profile intents can be created at this time.");
  }

  @Transactional
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
      userService.removeTraitsFromUser(intent.getUser().getId(), List.of("AWAITING_PROFILE_CREATION"));
      userService.addRoleToUser(intent.getUser().getId(), intent.getType(), response.entityId());

      log.info("Profile intent with ID {} processed successfully.", intent.getId());
    }

    profileIntentRepository.save(intent);
  }

  private void checkExistingIntent(User user, EnumRole type) {
    var existingIntent = profileIntentRepository
        .findCurrentByUserAndType(user, type, false);

    if (existingIntent.isPresent()) {
      throw new ConflictException(INTENT_ALREADY_EXISTS);
    }
  }

}
