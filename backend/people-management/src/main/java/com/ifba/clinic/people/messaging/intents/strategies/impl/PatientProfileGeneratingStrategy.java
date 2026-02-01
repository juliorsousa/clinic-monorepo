package com.ifba.clinic.people.messaging.intents.strategies.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifba.clinic.people.entities.enums.EnumBrazilState;
import com.ifba.clinic.people.messaging.intents.models.RunProfileIntentMessage;
import com.ifba.clinic.people.messaging.intents.models.requests.ProfileIntentRequest;
import com.ifba.clinic.people.messaging.intents.models.responses.ProfileIntentResponse;
import com.ifba.clinic.people.messaging.intents.strategies.ProfileGeneratingStrategy;
import com.ifba.clinic.people.models.error.ValidationError;
import com.ifba.clinic.people.models.requests.AddressRequest;
import com.ifba.clinic.people.models.requests.CreatePatientRequest;
import com.ifba.clinic.people.models.response.CreatePatientResponse;
import com.ifba.clinic.people.services.PatientService;
import com.ifba.clinic.people.utils.validation.ErrorUtils;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientProfileGeneratingStrategy implements ProfileGeneratingStrategy {

  private final PatientService patientService;
  private final ObjectMapper objectMapper;
  private final Validator validator;

  @Override
  public String getProfileType() {
    return "PATIENT";
  }

  @Override
  public ProfileIntentResponse generateProfile(RunProfileIntentMessage request) {
    log.info("Generating patient profile for userId: {}", request.userId());

    ValidationError validationError = validateRequest(request);

    if (validationError != null) {
      log.warn("Validation failed for userId {}: {}", request.userId(), validationError);

      return buildErrorResponse(request.intentId(), validationError);
    }

    try {
      ProfileIntentRequest.Personal personalInfo = request.body().personal();
      AddressRequest address = mapToAddress(personalInfo.address());

      CreatePatientRequest patientRequest = CreatePatientRequest.builder()
          .name(personalInfo.personal().name())
          .document(personalInfo.personal().document())
          .phone(personalInfo.personal().phone())
          .address(address)
          .build();

      CreatePatientResponse patientResponse = patientService.createPatient(patientRequest);

      log.info("Patient profile created successfully for userId: {}", request.userId());

      return new ProfileIntentResponse(
          request.intentId(),
          "PROCESSED",
          patientResponse.id(),
          "{}"
      );

    } catch (IllegalArgumentException e) {
      log.error("Invalid data for patient creation: {}", e.getMessage());
      return buildErrorResponse(request.intentId(), e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error creating patient profile: {}", e.getMessage(), e);
      return buildErrorResponse(request.intentId(), "Internal error while creating patient profile");
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

