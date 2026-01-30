package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Patient;
import com.ifba.clinic.people.models.requests.AddressRequest;

public record GetPatientResponse(String id,
                                 String name,
                                 String document,
                                 String email,
                                 AddressRequest address) {
  public static GetPatientResponse from(Patient patient) {
    return new GetPatientResponse(
        patient.getId(),
        patient.getName(),
        patient.getDocument(),
        patient.getEmail(),
        AddressRequest.fromAddress(patient.getAddress())
    );
  }
}
