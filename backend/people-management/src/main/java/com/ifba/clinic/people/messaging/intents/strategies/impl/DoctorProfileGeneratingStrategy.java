package com.ifba.clinic.people.messaging.intents.strategies.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifba.clinic.people.entities.Person;
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
import com.ifba.clinic.people.models.requests.person.CreatePersonRequest;
import com.ifba.clinic.people.models.response.GetDoctorResponse;
import com.ifba.clinic.people.repositories.PersonRepository;
import com.ifba.clinic.people.services.DoctorService;
import com.ifba.clinic.people.services.PersonService;
import com.ifba.clinic.people.utils.validation.ErrorUtils;
import jakarta.validation.Validator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DoctorProfileGeneratingStrategy implements ProfileGeneratingStrategy {

  private final DoctorService doctorService;

  private final PersonService personService;
  private final PersonRepository personRepository;

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
      Optional<Person> personOptional = personRepository.findByUserId(request.userId());

      if (personOptional.isEmpty()) {
        ProfileIntentRequest.Personal personalInfo = request.body().personal();
        AddressRequest address = mapToAddress(personalInfo.address());

        CreatePersonRequest personRequest = CreatePersonRequest.builder()
            .name(personalInfo.personal().name())
            .phone(personalInfo.personal().phone())
            .document(personalInfo.personal().document())
            .userId(request.userId())
            .address(address)
            .build();

        personService.createPerson(request.userId(), personRequest);

        personOptional = personRepository.findByUserId(request.userId());

        log.info("[ProfileGeneration] Person profile created successfully for userId: {}", request.userId());
      }

      if (personOptional.isEmpty()) {
        log.error("Failed to create or retrieve person profile for userId: {}", request.userId());

        return buildErrorResponse(request.intentId(), new MessagedError("Falha ao criar ou recuperar o perfil da pessoa."));
      }

      Person person = personOptional.get();

      ProfileIntentRequest.Specific specific = request.body().specific();

      CreateDoctorRequest doctorRequest = CreateDoctorRequest.builder()
          .credential(specific.credential())
          .speciality(specific.getSpecialtyEnum())
          .build();

      GetDoctorResponse doctorResponse = doctorService.createDoctor(person.getId(), doctorRequest);

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

