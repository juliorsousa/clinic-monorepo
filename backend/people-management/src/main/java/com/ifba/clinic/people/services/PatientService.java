package com.ifba.clinic.people.services;

import com.ifba.clinic.people.entities.Patient;
import com.ifba.clinic.people.entities.Person;
import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.exceptions.NotFoundException;
import com.ifba.clinic.people.feign.AppointmentClient;
import com.ifba.clinic.people.messaging.roles.models.UserRoleDroppedEvent;
import com.ifba.clinic.people.messaging.roles.producers.UserRoleProducer;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.response.GetPatientResponse;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.models.response.SummarizedPatientResponse;
import com.ifba.clinic.people.repositories.PatientRepository;
import com.ifba.clinic.people.repositories.PersonRepository;
import com.ifba.clinic.people.security.annotations.AuthRequired;
import com.ifba.clinic.people.security.annotations.RoleRestricted;
import com.ifba.clinic.people.security.components.AuthorizationComponent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import static com.ifba.clinic.people.utils.Messages.PATIENT_DUPLICATED;
import static com.ifba.clinic.people.utils.Messages.PATIENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

  private final PatientRepository patientRepository;
  private final PersonRepository personRepository;

  private final AuthorizationComponent authorizationComponent;
  private final AppointmentClient appointmentClient;
  private final PersonService personService;
  private final UserRoleProducer userRoleProducer;

  @AuthRequired
  @RoleRestricted("ADMIN")
  @Transactional
  public PageResponse<GetPatientResponse> listPatients(PageableRequest pageableRequest) {
    Pageable pageable = PageRequest.of(
        pageableRequest.page(),
        pageableRequest.size(),
        Sort.by("person.name").ascending()
    );

    Page<GetPatientResponse> patientPage = patientRepository.findAll(pageable)
        .map(GetPatientResponse::from);

    return PageResponse.from(patientPage);
  }

  @AuthRequired
  @Transactional
  public GetPatientResponse getPatientById(String id) {
    log.info("Fetching patient with id: {}", id);

    Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(PATIENT_NOT_FOUND));

    if (!authorizationComponent.hasPermissionToManageResource(patient.getPerson().getUserId())) {
      throw new NotFoundException(PATIENT_NOT_FOUND);
    }

    return GetPatientResponse.from(patient);
  }

  @AuthRequired
  @Transactional
  public SummarizedPatientResponse getSummarizedPatientById(String id) {
    log.info("Fetching summarized patient with id: {}", id);

    Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(PATIENT_NOT_FOUND));

    return new SummarizedPatientResponse(patient);
  }

  @Transactional
  public GetPatientResponse createPatient(String personId) {
    log.info("Creating patient for personId: {}", personId);

    Person person = personRepository.findById(personId)
        .orElseThrow(() -> new NotFoundException(PATIENT_NOT_FOUND));

    boolean patientAlreadyExists =
        patientRepository.findByPerson(person).isPresent();

    if (patientAlreadyExists) {
      throw new ConflictException(PATIENT_DUPLICATED);
    }

    Patient patient = Patient.from(person);
    Patient savedPatient = patientRepository.save(patient);

    log.info("Patient created with id: {}", savedPatient.getId());

    return GetPatientResponse.from(savedPatient);
  }

  @Transactional
  @AuthRequired
  @RoleRestricted("ADMIN")
  public void deletePatient(String id) {
    log.info("Deleting patient with id: {}", id);

    Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(PATIENT_NOT_FOUND));

    appointmentClient.deletePatientAppointments(id);

    patient.setDeleted(true);
    patientRepository.saveAndFlush(patient);

    String deletedRole = "PATIENT";

    personService.deleteIfNothingToParent(patient.getPerson().getId(), deletedRole);

    log.info("Patient with id: {} deleted successfully", id);

    userRoleProducer.publishRoleDropped(new UserRoleDroppedEvent(patient.getId(), deletedRole));
    log.info("Published UserRoleDroppedEvent for patient id: {}", patient.getId());
  }

}
