package com.ifba.clinic.people.services;

import com.ifba.clinic.people.entities.Doctor;
import com.ifba.clinic.people.entities.Person;
import com.ifba.clinic.people.entities.enums.EnumDoctorSpecialty;
import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.exceptions.NotFoundException;
import com.ifba.clinic.people.feign.AppointmentClient;
import com.ifba.clinic.people.messaging.roles.models.UserRoleDroppedEvent;
import com.ifba.clinic.people.messaging.roles.producers.UserRoleProducer;
import com.ifba.clinic.people.models.requests.CreateDoctorRequest;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.UpdateDoctorRequest;
import com.ifba.clinic.people.models.response.GetDoctorResponse;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.models.response.SummarizedDoctorResponse;
import com.ifba.clinic.people.repositories.DoctorRepository;
import com.ifba.clinic.people.repositories.PersonRepository;
import com.ifba.clinic.people.security.annotations.AuthRequired;
import com.ifba.clinic.people.security.annotations.RoleRestricted;
import com.ifba.clinic.people.security.components.AuthorizationComponent;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import static com.ifba.clinic.people.utils.Messages.DOCTOR_DUPLICATED;
import static com.ifba.clinic.people.utils.Messages.DOCTOR_NOT_FOUND;
import static com.ifba.clinic.people.utils.Messages.PERSON_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

  private final DoctorRepository doctorRepository;
  private final PersonRepository personRepository;

  private final AppointmentClient appointmentClient;
  private final AuthorizationComponent authorizationComponent;
  private final PersonService personService;
  private final UserRoleProducer userRoleProducer;

  @AuthRequired
  @RoleRestricted("ADMIN")
  @Transactional
  public PageResponse<GetDoctorResponse> listDoctors(PageableRequest pageableRequest) {
    Pageable pageable = PageRequest.of(
        pageableRequest.page(),
        pageableRequest.size(),
        Sort.by("person.name").ascending()
    );

    Page<GetDoctorResponse> doctorPage = doctorRepository.findAll(pageable)
        .map(GetDoctorResponse::new);

    return PageResponse.from(doctorPage);
  }

  @AuthRequired
  @Transactional
  public GetDoctorResponse getDoctorById(String id) {
    log.info("Fetching doctor with id: {}", id);

    Doctor doctor = doctorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(DOCTOR_NOT_FOUND));

    if (!authorizationComponent.hasPermissionToManageResource(doctor.getPerson().getUserId())) {
      log.warn("Unauthorized access attempt to doctor with id: {}", id);

      throw new NotFoundException(DOCTOR_NOT_FOUND);
    }

    return new GetDoctorResponse(doctor);
  }

  @AuthRequired
  @Transactional
  public SummarizedDoctorResponse getSummarizedDoctorById(String id) {
    log.info("Fetching summarized data of Doctor with id: {}", id);

    Doctor doctor = doctorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(DOCTOR_NOT_FOUND));

    return new SummarizedDoctorResponse(doctor);
  }

  @AuthRequired
  @Transactional
  public List<SummarizedDoctorResponse> getSummarizedDoctorsBySpecialty(EnumDoctorSpecialty specialty) {
    log.info("Fetching summarized data of Doctors with specialty: {}", specialty);

    List<Doctor> doctors = doctorRepository.findAllBySpecialty(specialty);

    return doctors.stream()
        .map(SummarizedDoctorResponse::new)
        .toList();
  }

  @Transactional
  public GetDoctorResponse createDoctor(String personId, CreateDoctorRequest request) {
    log.info("Creating doctor with credential: {} for personId: {}", request.credential(), personId);

    Person person = personRepository
        .findById(personId)
        .orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND));

    boolean doctorAlreadyExists =
        doctorRepository.findByCredentialOrPerson(
            request.credential(),
            person
        ).isPresent();

    if (doctorAlreadyExists) {
      throw new ConflictException(DOCTOR_DUPLICATED);
    }

    Doctor doctor = Doctor.fromCreationRequest(person, request);
    Doctor savedDoctor = doctorRepository.save(doctor);

    log.info("Doctor created with id: {}", savedDoctor.getId());

    return new GetDoctorResponse(savedDoctor);
  }

  @Transactional
  public void updateDoctor(String id, UpdateDoctorRequest request) {
    log.info("Updating doctor with id: {}", id);

    Doctor doctor = doctorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(DOCTOR_NOT_FOUND));

    if (!authorizationComponent.hasPermissionToManageResource(doctor.getPerson().getUserId())) {
      log.warn("Unauthorized update attempt to doctor with id: {}", id);

      throw new NotFoundException(DOCTOR_NOT_FOUND);
    }

    doctor.updateFromRequest(request);

    doctorRepository.save(doctor);

    log.info("Doctor with id: {} updated successfully", id);
  }

  @Transactional
  @RoleRestricted("ADMIN")
  public void deleteDoctor(String id) {
    log.info("Deleting doctor with id: {}", id);

    Doctor doctor = doctorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(DOCTOR_NOT_FOUND));

    appointmentClient.deleteDoctorAppointments(id);

    doctor.setDeleted(true);

    doctorRepository.saveAndFlush(doctor);

    String deletedRole = "DOCTOR";

    userRoleProducer.publishRoleDropped(new UserRoleDroppedEvent(doctor.getId(), deletedRole));
    personService.deleteIfNothingToParent(doctor.getPerson().getId(), deletedRole);

    log.info("Doctor with id: {} deleted successfully", id);
  }

  public Boolean validateDoctor(String id) {
    log.info("Validating doctor with id: {}", id);

    doctorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(DOCTOR_NOT_FOUND));

    log.info("Doctor with id: {} validated", id);

    return true;
  }

}