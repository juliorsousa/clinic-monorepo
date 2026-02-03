package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Patient;

public record SummarizedPatientResponse(
    String id,
    String name
) {
  public SummarizedPatientResponse(Patient doctor) {
    this(
        doctor.getId(),
        doctor.getPerson().getName()
    );
  }
}