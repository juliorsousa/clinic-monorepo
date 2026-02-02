package com.ifba.clinic.people.messaging.intents.strategies.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifba.clinic.people.entities.enums.EnumBrazilState;
import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.messaging.intents.models.RunProfileIntentMessage;
import com.ifba.clinic.people.messaging.intents.models.requests.ProfileIntentRequest;
import com.ifba.clinic.people.messaging.intents.models.responses.ProfileIntentResponse;
import com.ifba.clinic.people.messaging.intents.strategies.ProfileGeneratingStrategy;
import com.ifba.clinic.people.models.error.MessagedError;
import com.ifba.clinic.people.models.error.ValidationError;
import com.ifba.clinic.people.models.requests.AddressRequest;
import com.ifba.clinic.people.models.requests.CreateDoctorRequest;
import com.ifba.clinic.people.models.response.CreateDoctorResponse;
import com.ifba.clinic.people.services.DoctorService;
import com.ifba.clinic.people.utils.validation.ErrorUtils;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DoctorProfileGeneratingStrategy implements ProfileGeneratingStrategy {

  private final DoctorService doctorService;
  private final ObjectMapper objectMapper;
  private final Validator validator;

  @Override
  public String getProfileType() {
    return "DOCTOR";
  }

  @Override
  public ProfileIntentResponse generateProfile(RunProfileIntentMessage request) {
    log.info("Generating doctor profile for userId: {}", request.userId());

    ValidationError validationError = validateRequest(request);

    if (validationError != null) {
      log.warn("Validation failed for userId {}: {}", request.userId(), validationError);

      return buildErrorResponse(request.intentId(), validationError);
    }

    try {
      ProfileIntentRequest.Personal personalInfo = request.body().personal();
      ProfileIntentRequest.Specific specific = request.body().specific();

      AddressRequest address = mapToAddress(personalInfo.address());

      CreateDoctorRequest doctorRequest = CreateDoctorRequest.builder()
          .name(personalInfo.personal().name())
          .phone(personalInfo.personal().phone())
          .userId(request.userId())
          .credential(specific.credential())
          .speciality(specific.getSpecialtyEnum())
          .address(address)
          .build();

      CreateDoctorResponse doctorResponse = doctorService.createDoctor(doctorRequest);

      log.info("Doctor profile ({} - {}) created successfully for userId: {}", specific.credential(), specific.specialty(), request.userId());

      return new ProfileIntentResponse(
          request.intentId(),
          "PROCESSED",
          doctorResponse.id(),
          "{}"
      );

    }  catch (IllegalArgumentException e) {
      log.error("Invalid data for patient creation: {}", e.getMessage());
      return buildErrorResponse(request.intentId(), new MessagedError("Dados inválidos: " + e.getMessage()));
    } catch (ConflictException e) {
      log.error("Conflict error creating patient profile: {}", e.getMessage());
      return buildErrorResponse(request.intentId(), new MessagedError(e.getMessage()));
    } catch (Exception e) {
      log.error("Unexpected error creating patient profile: {}", e.getMessage(), e);
      return buildErrorResponse(request.intentId(), new MessagedError("Ocorreu um erro inesperado ao criar o perfil do médico."));
    }
  }

  private AddressRequest mapToAddress(ProfileIntentRequest.Address addr) {
    return AddressRequest.of(
        addr.street(),
        addr.house(),
        addr.complement(),
        addr.neighborhood(),
        addr.city(),
        EnumBrazilState.valueOf(addr.state()),
        addr.zipCode()
    );
  }

  private ValidationError validateRequest(RunProfileIntentMessage request) {
    var violations = validator.validate(request);
    return violations.isEmpty() ? null : ErrorUtils.from(violations);
  }

  private ProfileIntentResponse buildErrorResponse(String intentId, Object error) {
    return new ProfileIntentResponse(
        intentId,
        "ERRORED",
        null,
        serializeObject(error)
    );
  }

  private String serializeObject(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      log.error("Error serializing object: {}", e.getMessage(), e);
      return "{\"message\":\"Serialization error\"}";
    }
  }
}

