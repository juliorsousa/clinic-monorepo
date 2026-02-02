package com.ifba.appointment.models.response;

import java.time.LocalDateTime;

import com.ifba.appointment.entities.Appointment;

public record CreateAppointmentResponse(
    String id,
    String idPatient,
    String idDoctor,
    LocalDateTime dateTime
) {
  public CreateAppointmentResponse(Appointment appointment) {
    this(
        appointment.getId(),
        appointment.getIdPatient(),
        appointment.getIdDoctor(),
        appointment.getDateTime()
    );
  }
}
