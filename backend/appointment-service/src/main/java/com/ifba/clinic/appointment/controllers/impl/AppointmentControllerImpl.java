package com.ifba.clinic.appointment.controllers.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ifba.clinic.appointment.controllers.AppointmentController;
import com.ifba.clinic.appointment.models.request.CreateAppointmentRequest;
import com.ifba.clinic.appointment.models.request.DatePeriodRequest;
import com.ifba.clinic.appointment.models.request.PageableRequest;
import com.ifba.clinic.appointment.models.response.CreateAppointmentResponse;
import com.ifba.clinic.appointment.models.response.GetAppointmentResponse;
import com.ifba.clinic.appointment.models.response.PageResponse;
import com.ifba.clinic.appointment.services.AppointmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AppointmentControllerImpl implements AppointmentController {

    private final AppointmentService appointmentService;

    @Override
    public PageResponse<GetAppointmentResponse> listAllAppointments(PageableRequest pageable) {
        return appointmentService.listAllAppointments(pageable);
    }

    @Override
    public PageResponse<GetAppointmentResponse> listPatientAppointments(String id, PageableRequest pageable, DatePeriodRequest request) {
        return appointmentService.listAllPatientAppointments(id, pageable, request);
    }

    @Override
    public PageResponse<GetAppointmentResponse> listDoctorDateAppointments(String id, PageableRequest pageable, DatePeriodRequest request) {
        return appointmentService.listAllDoctorInPeriodAppointments(id, pageable, request);
    }

    @Override
    public ResponseEntity<CreateAppointmentResponse> createAppointment(CreateAppointmentRequest request) {
        return ResponseEntity.status(201).body(appointmentService.createAppointment(request));
    }

    @Override
    public ResponseEntity<Void> cancelAppointment(String id) {
            appointmentService.cancelAppointment(id);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @Override
    public ResponseEntity<Void> deleteAllDoctorAppointments(String id) {
            appointmentService.deleteDoctorAppointments(id);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @Override
    public ResponseEntity<Void> deleteAllPatientAppointments(String id) {
            appointmentService.deletePatientAppointments(id);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }
}
