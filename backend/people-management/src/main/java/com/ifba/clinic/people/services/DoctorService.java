package com.ifba.clinic.people.services;

import com.ifba.clinic.people.entities.Address;
import com.ifba.clinic.people.entities.Doctor;
import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.exceptions.NotFoundException;
import com.ifba.clinic.people.models.requests.CreateDoctorRequest;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.UpdateDoctorRequest;
import com.ifba.clinic.people.models.response.CreateDoctorResponse;
import com.ifba.clinic.people.models.response.GetDoctorResponse;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.repositories.AddressRepository;
import com.ifba.clinic.people.repositories.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.ifba.clinic.people.utils.Messages.DOCTOR_DUPLICATED;
import static com.ifba.clinic.people.utils.Messages.DOCTOR_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

  private final DoctorRepository doctorRepository;
  private final AddressRepository addressRepository;

  public PageResponse<GetDoctorResponse> listDoctors(PageableRequest pageableRequest) {
    Pageable pageable = PageRequest.of(
        pageableRequest.page(),
        pageableRequest.size(),
        Sort.by("name").ascending()
    );

    Page<GetDoctorResponse> doctorPage = doctorRepository.findAll(pageable)
        .map(GetDoctorResponse::from);

    return PageResponse.from(doctorPage);
  }

  @Transactional
  public CreateDoctorResponse createDoctor(CreateDoctorRequest request) {
    log.info("Creating doctor with credential: {}", request.credential());

    boolean doctorAlreadyExists =
        doctorRepository.findByCredential(
            request.credential()
        ).isPresent();

    if (doctorAlreadyExists) {
      throw new ConflictException(DOCTOR_DUPLICATED);
    }

    Address address = Address.fromCreationRequest(request.address());
    Address savedAddress = addressRepository.save(address);

    Doctor doctor = Doctor.fromCreationRequest(request, savedAddress);
    Doctor savedDoctor = doctorRepository.save(doctor);

    log.info("Doctor created with id: {}", savedDoctor.getId());

    return new CreateDoctorResponse(savedDoctor);
  }

  @Transactional
  public void updateDoctor(String id, UpdateDoctorRequest request) {
    log.info("Updating doctor with id: {}", id);

    Doctor doctor = doctorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(DOCTOR_NOT_FOUND));

    doctor.updateFromRequest(request);

    doctorRepository.save(doctor);

    log.info("Doctor with id: {} updated successfully", id);
  }

  @Transactional
  public void deleteDoctor(String id) {
    log.info("Deleting doctor with id: {}", id);

    Doctor doctor = doctorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(DOCTOR_NOT_FOUND));

    doctorRepository.delete(doctor);

    log.info("Doctor with id: {} deleted successfully", id);
  }

  public Boolean isAvaiable(String id) {
    log.info("validating doctor with id: {}", id);

    var doctor = doctorRepository.findById(id)
                  .orElseThrow(() -> new NotFoundException(DOCTOR_NOT_FOUND));

    log.info("doctor with id: {} validated", id);
    return true;
  }
}