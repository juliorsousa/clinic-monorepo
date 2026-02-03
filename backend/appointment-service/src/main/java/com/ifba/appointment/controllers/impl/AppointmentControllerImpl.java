package com.ifba.appointment.controllers.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ifba.appointment.controllers.AppointmentController;
import com.ifba.appointment.models.request.CreateAppointmentRequest;
import com.ifba.appointment.models.request.DatePeriodRequest;
import com.ifba.appointment.models.request.PageableRequest;
import com.ifba.appointment.models.response.CreateAppointmentResponse;
import com.ifba.appointment.models.response.GetAppoitmentResponse;
import com.ifba.appointment.models.response.PageResponse;
import com.ifba.appointment.services.AppointmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AppointmentControllerImpl implements AppointmentController {

    private final AppointmentService appointmentService;

    @Override
    public PageResponse<GetAppoitmentResponse> listAllAppointments(PageableRequest pageable) {
        return appointmentService.listAllAppointments(pageable);
    }

    @Override
    public PageResponse<GetAppoitmentResponse> listDoctorAppointments(PageableRequest pageable) {
        return appointmentService.listAllDoctorAppointments(pageable);
    }

    @Override
    public PageResponse<GetAppoitmentResponse> listPacientAppointments(PageableRequest pageable) {
        return appointmentService.listAllPatientAppointments(pageable);
    }

    @Override
    public PageResponse<GetAppoitmentResponse> listDoctorDateAppointments(PageableRequest pageable, DatePeriodRequest request) {
        return appointmentService.listAllDoctorInPeriodAppointments(pageable, request);
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
    public ResponseEntity<Void> deleteAllDoctorAppointment(String id) {
            appointmentService.deleteDoctorAppointments(id);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @Override
    public ResponseEntity<Void> deleteAllPatientAppointment(String id) {
            appointmentService.deletePatientAppointments(id);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }
}
