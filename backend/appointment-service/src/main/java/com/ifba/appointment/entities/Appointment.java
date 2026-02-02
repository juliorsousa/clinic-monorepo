package com.ifba.appointment.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import com.ifba.appointment.entities.enums.AppointmentStatus;
import com.ifba.appointment.models.request.CreateAppointmentRequest;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "TAPPOINTMENT")
@SQLDelete(sql = "UPDATE APPOINTMENT SET IN_DELETED = true WHERE ID_APPOINTMENT = ?")
@SQLRestriction("IN_DELETED = false")

public class Appointment {
    
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "ID_APPOINTMENT")
    private String id;

    @Column(name = "ID_PATIENT")
    private String idPatient;

    @Column(name = "ID_DOCTOR")
    private String idDoctor;

    @Column(name = "DATE_TIME_APPOINTMENT")
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_STATUS")
    private AppointmentStatus status;

    @Column(name = "IN_DELETED")
    private boolean deleted = Boolean.FALSE;

    public static Appointment fromCreationRequest(CreateAppointmentRequest request, String patient) {
        return Appointment.builder()
            .idPatient(patient)
            .idDoctor(request.idDoctor())
            .dateTime(request.dateTime())
            .status(AppointmentStatus.PENDING)
            .build();
    }
}

