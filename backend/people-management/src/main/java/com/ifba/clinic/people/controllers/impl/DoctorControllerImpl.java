package com.ifba.clinic.people.controllers.impl;

import com.ifba.clinic.people.controllers.DoctorController;
import com.ifba.clinic.people.models.requests.CreateDoctorRequest;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.UpdateDoctorRequest;
import com.ifba.clinic.people.models.response.GetDoctorResponse;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.models.response.SummarizedDoctorResponse;
import com.ifba.clinic.people.services.DoctorService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DoctorControllerImpl implements DoctorController {

  private final DoctorService doctorService;

  @Override
  public PageResponse<GetDoctorResponse> listDoctors(PageableRequest pageable) {
    return doctorService.listDoctors(pageable);
  }

  @Override
  public GetDoctorResponse getDoctorById(String id) {
    return doctorService.getDoctorById(id);
  }

  @Override
  public SummarizedDoctorResponse getSummarizedDoctorById(String id) {
    return doctorService.getSummarizedDoctorById(id);
  }

  @Override
  public ResponseEntity<Void> updateDoctor(String id, UpdateDoctorRequest request) {
    doctorService.updateDoctor(id, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }

  @Override
  public ResponseEntity<Void> deleteDoctor(String id) {
    doctorService.deleteDoctor(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Override
  public ResponseEntity<Boolean> validateDoctor(String id) {
      return ResponseEntity.ok(doctorService.validateDoctor(id));
  }
}