package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Patient;
import com.ifba.clinic.people.models.response.person.GetPersonResponse;

public record GetPatientResponse(String id, GetPersonResponse person) {
  public static GetPatientResponse from(Patient patient) {
    return new GetPatientResponse(
        patient.getId(),
        GetPersonResponse.from(patient.getPerson())
    );
  }
}
