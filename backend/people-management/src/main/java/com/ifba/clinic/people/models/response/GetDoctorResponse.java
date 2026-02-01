package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Doctor;
import com.ifba.clinic.people.entities.enums.EnumDoctorSpeciality;
import com.ifba.clinic.people.models.requests.AddressRequest;

public record GetDoctorResponse(String id,
                                String name,
                                String credential,
                                String email,
                                EnumDoctorSpeciality speciality,
                                AddressRequest address) {

  public static GetDoctorResponse from(Doctor doctor) {
    return new GetDoctorResponse(
        doctor.getId(),
        doctor.getName(),
        doctor.getCredential(),
        doctor.getEmail(),
        doctor.getSpeciality(),
        // Assume que o método estático fromAddress existe no AddressRequest, 
        // conforme seu exemplo do Patient
        AddressRequest.fromAddress(doctor.getAddress())
    );
  }
}