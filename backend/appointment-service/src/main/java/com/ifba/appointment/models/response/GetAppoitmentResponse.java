package com.ifba.appointment.models.response;

import java.time.LocalDateTime;

import com.ifba.appointment.entities.Appointment;

public record GetAppoitmentResponse(String id,
                                    String idPatient,
                                    String idDoctor,
                                    LocalDateTime dateTime) {
  
  public static GetAppoitmentResponse from(Appointment appointment) {
    return new GetAppoitmentResponse(
        appointment.getId(),
        appointment.getIdPatient(),
        appointment.getIdDoctor(),
        appointment.getDateTime()
    );
  }
}
