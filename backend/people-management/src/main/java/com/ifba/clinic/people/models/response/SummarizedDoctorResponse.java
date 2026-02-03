package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Doctor;
import com.ifba.clinic.people.entities.enums.EnumDoctorSpeciality;

public record SummarizedDoctorResponse(
    String credential,
    String name,
    EnumDoctorSpeciality specialty
    ) {
  public SummarizedDoctorResponse(Doctor doctor) {
    this(
        doctor.getCredential(),
        doctor.getPerson().getName(),
        doctor.getSpeciality()
    );
  }
}