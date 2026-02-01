package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Doctor;
import com.ifba.clinic.people.entities.enums.EnumDoctorSpeciality;

public record CreateDoctorResponse(
    String id,
    String name,
    String email,
    String phone,
    String credential,
    EnumDoctorSpeciality speciality,
    AddressResponse address
) {
  public CreateDoctorResponse(Doctor doctor) {
    this(
        doctor.getId(),
        doctor.getName(),
        doctor.getEmail(),
        doctor.getPhone(),
        doctor.getCredential(),
        doctor.getSpeciality(),
        new AddressResponse(doctor.getAddress())
    );
  }
}