package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Doctor;
import com.ifba.clinic.people.entities.enums.EnumDoctorSpeciality;
import com.ifba.clinic.people.models.response.person.GetPersonResponse;

public record GetDoctorResponse(
    String id,
    String credential,
    EnumDoctorSpeciality speciality,
    GetPersonResponse person
) {
  public GetDoctorResponse(Doctor doctor) {
    this(
        doctor.getId(),
        doctor.getCredential(),
        doctor.getSpeciality(),
        GetPersonResponse.from(doctor.getPerson())
    );
  }
}