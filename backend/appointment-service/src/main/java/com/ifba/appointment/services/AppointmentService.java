package com.ifba.appointment.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ifba.appointment.entities.Appointment;
import com.ifba.appointment.entities.enums.AppointmentStatus;
import com.ifba.appointment.exceptions.ConflictException;
import com.ifba.appointment.exceptions.NotFoundException;
import com.ifba.appointment.feign.DoctorClient;
import com.ifba.appointment.models.request.CreateAppointmentRequest;
import com.ifba.appointment.models.request.DatePeriodRequest;
import com.ifba.appointment.models.request.PageableRequest;
import com.ifba.appointment.models.response.CreateAppointmentResponse;
import com.ifba.appointment.models.response.GetAppoitmentResponse;
import com.ifba.appointment.models.response.PageResponse;
import com.ifba.appointment.repository.AppointmentRepository;
import com.ifba.appointment.security.annotations.AuthRequired;
import com.ifba.appointment.security.annotations.RoleRestricted;
import com.ifba.appointment.security.models.UserContext;
import com.ifba.appointment.utils.Messages;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final DoctorClient doctorClient;

    @AuthRequired
    @RoleRestricted("PATIENT") // just to test (ADMIN)
    public PageResponse<GetAppoitmentResponse> listAllAppointments(PageableRequest pageableRequest) {
        Pageable pageable = PageRequest.of(
            pageableRequest.page(),
            pageableRequest.size(),
            Sort.by("dateTime").ascending()
        );

        Page<GetAppoitmentResponse> appointmentPage = appointmentRepository.findAll(pageable)
            .map(GetAppoitmentResponse::from);

        return PageResponse.from(appointmentPage);
    }

    @AuthRequired
    @RoleRestricted("PATIENT")
    public PageResponse<GetAppoitmentResponse> listAllPatientAppointments(PageableRequest pageableRequest) {
        Pageable pageable = PageRequest.of(
            pageableRequest.page(),
            pageableRequest.size(),
            Sort.by("dateTime").ascending()
        );

        Page<GetAppoitmentResponse> patientPage = appointmentRepository.findAllByIdPatient(pageable, UserContext.getUserId())
            .map(GetAppoitmentResponse::from);

        return PageResponse.from(patientPage);
    }

    @AuthRequired
    @RoleRestricted("DOCTOR") 
    public PageResponse<GetAppoitmentResponse> listAllDoctorAppointments(PageableRequest pageableRequest) {
        Pageable pageable = PageRequest.of(
            pageableRequest.page(),
            pageableRequest.size(),
            Sort.by("dateTime").ascending()
        );

        Page<GetAppoitmentResponse> doctorPage = appointmentRepository.findAllByIdDoctor(pageable, UserContext.getUserId())
            .map(GetAppoitmentResponse::from);

        return PageResponse.from(doctorPage);
    }

    @AuthRequired
    @RoleRestricted("DOCTOR")
    public PageResponse<GetAppoitmentResponse> listAllDoctorInPeriodAppointments(PageableRequest pageableRequest, DatePeriodRequest request) {
        Pageable pageable = PageRequest.of(
            pageableRequest.page(),
            pageableRequest.size(),
            Sort.by("dateTime").ascending()
        );

        LocalDateTime startDateTime = request.startDateTime(); 
        LocalDateTime finishDateTime = request.finishDateTime();

        Page<GetAppoitmentResponse> doctorPage = appointmentRepository.findAllBetweenDatePeriod(pageable, startDateTime, 
                                                                                                finishDateTime, UserContext.getUserId())
            .map(GetAppoitmentResponse::from);

        return PageResponse.from(doctorPage);
    }

    @AuthRequired
    @RoleRestricted("PATIENT")
    @Transactional
    public CreateAppointmentResponse createAppointment(CreateAppointmentRequest request) {

        String patientId = UserContext.getUserId();
        
        doctorClient.isDoctorAvailable(request.idDoctor());

        LocalDateTime date = request.dateTime(); 

        if (appointmentRepository.existsConflictByPatientInDateTime(patientId, date))
            throw new ConflictException("The patient already has other appointment that day");

        LocalDateTime oneHourLater = request.dateTime().plusMinutes(59); 
        LocalDateTime oneHourbefore = request.dateTime().minusMinutes(59); 

        if (appointmentRepository.existsConflictByDoctorInDateTime(request.idDoctor(), oneHourbefore, oneHourLater))
            throw new ConflictException("The doctor already has other appointment near that time");

        Appointment appointment = Appointment.fromCreationRequest(request, patientId);

        return new CreateAppointmentResponse(appointmentRepository.save(appointment));
    }

    @AuthRequired
    @RoleRestricted("PATIENT")  // Não sei lógica
    @Transactional
    public void cancelAppointment(String id) {
        log.info("Cancelling appointment with id: {}", id);

        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(Messages.APPOINTMENT_NOT_FOUND));

        appointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentRepository.save(appointment);

        log.info("Appoinment with id: {} cancelled successfully", id);
    }

    @AuthRequired
    @RoleRestricted("PATIENT")
    @Transactional
    public void deletePatientAppointments() {
        log.info("Deleting appointment with id: {}", UserContext.getUserId());

        Appointment appointment = appointmentRepository.findById(UserContext.getUserId())
            .orElseThrow(() -> new NotFoundException(Messages.APPOINTMENT_NOT_FOUND));

        appointmentRepository.delete(appointment);

        log.info("Appoinment with id: {} deleted successfully", UserContext.getUserId());
    }

    @AuthRequired
    @RoleRestricted("DOCTOR")
    @Transactional
    public void deleteDoctorAppointments() {
        log.info("Deleting appointment with id: {}", UserContext.getUserId());

        Appointment appointment = appointmentRepository.findById(UserContext.getUserId())
            .orElseThrow(() -> new NotFoundException(Messages.APPOINTMENT_NOT_FOUND));

        appointmentRepository.delete(appointment);

        log.info("Appoinment with id: {} deleted successfully", UserContext.getUserId());
    }
}
