package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Doctor;
import com.ifba.clinic.people.entities.enums.EnumDoctorSpecialty;

public record SummarizedDoctorResponse(
    String id,
    String credential,
    String name,
    EnumDoctorSpecialty specialty
    ) {
  public SummarizedDoctorResponse(Doctor doctor) {
    this(
        doctor.getId(),
        doctor.getCredential(),
        doctor.getPerson().getName(),
        doctor.getSpecialty()
    );
  }
}