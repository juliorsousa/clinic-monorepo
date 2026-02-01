package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Patient;

public record CreatePatientResponse(
    String id,
    String name,
    String document,
    String phone,
    AddressResponse address
) {
  public CreatePatientResponse(Patient patient) {
    this(
        patient.getId(),
        patient.getName(),
        patient.getDocument(),
        patient.getPhone(),
        new AddressResponse(patient.getAddress())
    );
  }
}
