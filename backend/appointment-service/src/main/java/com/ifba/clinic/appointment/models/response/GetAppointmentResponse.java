package com.ifba.clinic.appointment.models.response;

import com.ifba.clinic.appointment.entities.Appointment;
import com.ifba.clinic.appointment.entities.enums.AppointmentStatus;
import java.time.LocalDateTime;

public record GetAppointmentResponse(
    String id,
    String patientId,
    String doctorId,

    AppointmentStatus status,
    String observation,
    LocalDateTime scheduledTo
) {
  public static GetAppointmentResponse from(Appointment appointment) {
    return new GetAppointmentResponse(
        appointment.getId(),
        appointment.getPatientId(),
        appointment.getDoctorId(),
        appointment.getStatus(),
        appointment.getObservation(),
        appointment.getScheduledTo()
    );
  }
}
