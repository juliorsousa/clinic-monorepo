package com.ifba.clinic.appointment.models.response;

import java.time.LocalDateTime;

import com.ifba.clinic.appointment.entities.Appointment;

public record CreateAppointmentResponse(
    String id,
    String idPatient,
    String idDoctor,
    LocalDateTime dateTime
) {
  public CreateAppointmentResponse(Appointment appointment) {
    this(
        appointment.getId(),
        appointment.getPatientId(),
        appointment.getDoctorId(),
        appointment.getScheduledTo()
    );
  }
}
