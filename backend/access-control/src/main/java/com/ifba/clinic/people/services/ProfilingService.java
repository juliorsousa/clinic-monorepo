package com.ifba.clinic.people.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifba.clinic.people.entities.ProfileIntent;
import com.ifba.clinic.people.entities.User;
import com.ifba.clinic.people.entities.enums.EnumIntentStatus;
import com.ifba.clinic.people.entities.enums.EnumRole;
import com.ifba.clinic.people.exceptions.BadRequestException;
import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.exceptions.NoContentException;
import com.ifba.clinic.people.exceptions.UnauthorizedException;
import com.ifba.clinic.people.models.requests.ChangePasswordRequest;
import com.ifba.clinic.people.models.requests.profiles.ProfileIntentRequest;
import com.ifba.clinic.people.models.response.ProfileIntentResponse;
import com.ifba.clinic.people.repositories.ProfileIntentRepository;
import com.ifba.clinic.people.repositories.UserRepository;
import com.ifba.clinic.people.security.annotations.AuthRequired;
import com.ifba.clinic.people.security.annotations.RoleRestricted;
import com.ifba.clinic.people.security.services.AuthenticationService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.ifba.clinic.people.utils.Messages.GENERIC_BAD_REQUEST;
import static com.ifba.clinic.people.utils.Messages.GENERIC_NO_CONTENT;
import static com.ifba.clinic.people.utils.Messages.INTENT_ALREADY_EXISTS;
import static com.ifba.clinic.people.utils.Messages.PASSWORD_MUST_BE_DIFFERENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilingService {

  private final AuthenticationService authenticationService;
  private final ProfileIntentRepository profileIntentRepository;
  private final UserService userService;

  private final ObjectMapper objectMapper;

  @Transactional
  @AuthRequired
  public ProfileIntentResponse getCurrentProfileIntent() {
    User currentUser = userService.getCurrentUser();

    log.info("Fetching current profile intent for user with email: {}", currentUser.getEmail());

    var existingIntent = profileIntentRepository
        .findCurrentByUser(currentUser, true);

    if (existingIntent.isPresent()) {
      ProfileIntent intent = existingIntent.get();

      log.info("Current profile intent found with ID: {}", intent.getId());

      return ProfileIntentResponse.builder()
          .id(intent.getId())
          .status(intent.getStatus())
          .type(intent.getType())
          .build();
    }

    log.info("No current profile intent found for user with email: {}", currentUser.getEmail());

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

      return ProfileIntentResponse.builder()
          .id(saved.getId())
          .status(saved.getStatus())
          .type(saved.getType())
          .build();
    }

    throw new BadRequestException("Only patient profile intents can be created at this time.");
  }

  private void checkExistingIntent(User user, EnumRole type) {
    var existingIntent = profileIntentRepository
        .findCurrentByUserAndType(user, type, false);

    if (existingIntent.isPresent()) {
      throw new ConflictException(INTENT_ALREADY_EXISTS);
    }
  }

}
