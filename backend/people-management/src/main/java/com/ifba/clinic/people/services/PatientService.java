package com.ifba.clinic.people.services;

import com.ifba.clinic.people.entities.Address;
import com.ifba.clinic.people.entities.Patient;
import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.exceptions.NotFoundException;
import com.ifba.clinic.people.models.requests.CreatePatientRequest;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.UpdatePatientRequest;
import com.ifba.clinic.people.models.response.CreatePatientResponse;
import com.ifba.clinic.people.models.response.GetPatientResponse;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.repositories.AddressRepository;
import com.ifba.clinic.people.repositories.PatientRepository;
import com.ifba.clinic.people.security.annotations.AuthRequired;
import com.ifba.clinic.people.security.annotations.RoleRestricted;
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
  private final AddressRepository addressRepository;

  @AuthRequired
  @RoleRestricted("ADMIN")
  public PageResponse<GetPatientResponse> listPatients(PageableRequest pageableRequest) {
    Pageable pageable = PageRequest.of(
        pageableRequest.page(),
        pageableRequest.size(),
        Sort.by("name").ascending()
    );

    Page<GetPatientResponse> patientPage = patientRepository.findAll(pageable)
        .map(GetPatientResponse::from);

    return PageResponse.from(patientPage);
  }

  @Transactional
  public CreatePatientResponse createPatient(CreatePatientRequest request) {
    log.info("Creating patient with document: {}", request.document());

    boolean patientAlreadyExists =
        patientRepository.findByDocument(
            request.document()
        ).isPresent();

    if (patientAlreadyExists) {
      throw new ConflictException(PATIENT_DUPLICATED);
    }

    Address address = Address.fromCreationRequest(request.address());
    Address savedAddress = addressRepository.save(address);

    Patient patient = Patient.fromCreationRequest(request, savedAddress);
    Patient savedPatient = patientRepository.save(patient);

    log.info("Patient created with id: {}", savedPatient.getId());

    return new CreatePatientResponse(savedPatient);
  }

  @Transactional
  public void updatePatient(String id, UpdatePatientRequest request) {
    log.info("Updating patient with id: {}", id);

    Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(PATIENT_NOT_FOUND));

    patient.updateFromRequest(request);

    patientRepository.save(patient);

    log.info("Patient with id: {} updated successfully", id);
  }

  @Transactional
  public void deletePatient(String id) {
    log.info("Deleting patient with id: {}", id);

    Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(PATIENT_NOT_FOUND));

    patientRepository.delete(patient);

    log.info("Patient with id: {} deleted successfully", id);
  }

}
