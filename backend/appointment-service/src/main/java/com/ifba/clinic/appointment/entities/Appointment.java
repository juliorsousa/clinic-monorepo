package com.ifba.clinic.appointment.entities;

import com.ifba.clinic.appointment.entities.enums.AppointmentStatus;
import com.ifba.clinic.appointment.models.request.CreateAppointmentRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "TAPPOINTMENTS")
@SQLDelete(sql = "UPDATE TAPPOINTMENTS SET IN_DELETED = true WHERE ID_APPOINTMENT = ?")
@SQLRestriction("IN_DELETED = false")
public class Appointment {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "ID_APPOINTMENT")
  private String id;

  @Column(name = "ID_PATIENT", nullable = false)
  private String patientId;

  @Column(name = "ID_DOCTOR", nullable = false)
  private String doctorId;

  @Column(name = "DT_SCHEDULING", nullable = false)
  private LocalDateTime scheduledTo;

  @Column(name = "IN_CANCELLED", nullable = false)
  private boolean cancelled = false;

  @Column(name = "VL_OBSERVATION")
  private String observation;

  @Column(name = "DT_CREATED", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "IN_DELETED")
  private boolean deleted = Boolean.FALSE;

  @PrePersist
  protected void prePersist() {
    createdAt = LocalDateTime.now();
  }

  public AppointmentStatus getStatus() {
    if (this.cancelled) {
      return AppointmentStatus.CANCELLED;
    }

    if (this.scheduledTo.plusHours(1).isBefore(LocalDateTime.now())) {
      return AppointmentStatus.COMPLETED;
    }

    if (this.scheduledTo.isBefore(LocalDateTime.now().plusHours(24))) {
      return AppointmentStatus.CONFIRMED;
    }

    if (this.scheduledTo.isBefore(LocalDateTime.now()) &&
        this.scheduledTo.plusHours(1).isAfter(LocalDateTime.now())) {
      return AppointmentStatus.ONGOING;
    }

    return AppointmentStatus.SCHEDULED;
  }

  public static Appointment fromCreationRequest(CreateAppointmentRequest request, String patient) {
    return Appointment.builder()
        .patientId(patient)
        .doctorId(request.doctorId())
        .scheduledTo(request.dateTime())
        .cancelled(false)
        .deleted(false)
        .build();
  }
}

