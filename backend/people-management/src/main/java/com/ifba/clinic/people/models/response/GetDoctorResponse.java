package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Doctor;
import com.ifba.clinic.people.entities.enums.EnumDoctorSpeciality;
import com.ifba.clinic.people.models.requests.AddressRequest;

public record GetDoctorResponse(String id,
                                String name,
                                String credential,
                                EnumDoctorSpeciality speciality,
                                AddressRequest address) {

  public static GetDoctorResponse from(Doctor doctor) {
    return new GetDoctorResponse(
        doctor.getId(),
        doctor.getName(),
        doctor.getCredential(),
        doctor.getSpeciality(),
        AddressRequest.fromAddress(doctor.getAddress())
    );
  }
}