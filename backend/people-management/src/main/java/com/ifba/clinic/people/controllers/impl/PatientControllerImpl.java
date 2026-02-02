package com.ifba.clinic.people.controllers.impl;

import com.ifba.clinic.people.controllers.PatientController;
import com.ifba.clinic.people.models.requests.CreatePatientRequest;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.UpdatePatientRequest;
import com.ifba.clinic.people.models.response.CreatePatientResponse;
import com.ifba.clinic.people.models.response.GetPatientResponse;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.services.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PatientControllerImpl implements PatientController {

  private final PatientService patientService;

  public ResponseEntity<CreatePatientResponse> createPatient(
      CreatePatientRequest request
  ) {
    var created = patientService.createPatient(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(created);
  }

  public PageResponse<GetPatientResponse> listPatients(PageableRequest pageable) {
    return patientService.listPatients(pageable);
  }

  public GetPatientResponse getPatientById(String id) {
    return patientService.getPatientById(id);
  }

  public ResponseEntity<Void> updatePatient(String id, UpdatePatientRequest request) {
    patientService.updatePatient(id, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }

  public ResponseEntity<Void> deletePatient(String id) {
    patientService.deletePatient(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
