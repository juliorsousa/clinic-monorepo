package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Doctor;
import com.ifba.clinic.people.entities.enums.EnumDoctorSpecialty;
import com.ifba.clinic.people.models.response.person.GetPersonResponse;

public record GetDoctorResponse(
    String id,
    String credential,
    EnumDoctorSpecialty specialty,
    GetPersonResponse person
) {
  public GetDoctorResponse(Doctor doctor) {
    this(
        doctor.getId(),
        doctor.getCredential(),
        doctor.getSpecialty(),
        GetPersonResponse.from(doctor.getPerson())
    );
  }
}