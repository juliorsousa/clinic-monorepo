package com.ifba.clinic.people.controllers.impl;

import com.ifba.clinic.people.controllers.PatientController;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.response.GetPatientResponse;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.models.response.SummarizedPatientResponse;
import com.ifba.clinic.people.services.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PatientControllerImpl implements PatientController {

  private final PatientService patientService;

  public PageResponse<GetPatientResponse> listPatients(PageableRequest pageable) {
    return patientService.listPatients(pageable);
  }

  public GetPatientResponse getPatientById(String id) {
    return patientService.getPatientById(id);
  }

  public SummarizedPatientResponse getSummarizedPatientById(String id) {
    return patientService.getSummarizedPatientById(id);
  }

  public ResponseEntity<Void> deletePatient(String id) {
    patientService.deletePatient(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
