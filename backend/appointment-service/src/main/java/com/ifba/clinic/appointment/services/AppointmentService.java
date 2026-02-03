package com.ifba.clinic.appointment.services;

import com.ifba.clinic.appointment.entities.Appointment;
import com.ifba.clinic.appointment.exceptions.ConflictException;
import com.ifba.clinic.appointment.exceptions.ForbiddenException;
import com.ifba.clinic.appointment.exceptions.NotFoundException;
import com.ifba.clinic.appointment.feign.DoctorsClient;
import com.ifba.clinic.appointment.models.request.CreateAppointmentRequest;
import com.ifba.clinic.appointment.models.request.DatePeriodRequest;
import com.ifba.clinic.appointment.models.request.PageableRequest;
import com.ifba.clinic.appointment.models.response.CreateAppointmentResponse;
import com.ifba.clinic.appointment.models.response.GetAppointmentResponse;
import com.ifba.clinic.appointment.models.response.PageResponse;
import com.ifba.clinic.appointment.models.response.UserRole;
import com.ifba.clinic.appointment.repository.AppointmentRepository;
import com.ifba.clinic.appointment.security.annotations.AuthRequired;
import com.ifba.clinic.appointment.security.annotations.RoleRestricted;
import com.ifba.clinic.appointment.security.models.UserContext;
import com.ifba.clinic.appointment.utils.Messages;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final DoctorsClient doctorsClient;

  @AuthRequired
  @RoleRestricted("ADMIN")
  public PageResponse<GetAppointmentResponse> listAllAppointments(PageableRequest pageableRequest) {
    Pageable pageable = PageRequest.of(
        pageableRequest.page(),
        pageableRequest.size(),
        Sort.by("scheduledTo").descending()
    );

    Page<GetAppointmentResponse> appointmentPage = appointmentRepository.findAll(pageable)
        .map(GetAppointmentResponse::from);

    return PageResponse.from(appointmentPage);
  }

  @AuthRequired
  public PageResponse<GetAppointmentResponse> listAllPatientAppointments(String id, PageableRequest pageableRequest, DatePeriodRequest request) {
    if (!hasPermissionToManageResource(id)) {
      throw new ForbiddenException();
    }

    Pageable pageable = PageRequest.of(
        pageableRequest.page(),
        pageableRequest.size(),
        Sort.by("scheduledTo").descending()
    );

    Page<GetAppointmentResponse> patientPage = appointmentRepository.findAllByPatientId(id, pageable)
        .map(GetAppointmentResponse::from);

    return PageResponse.from(patientPage);
  }

  @AuthRequired
  public PageResponse<GetAppointmentResponse> listAllDoctorInPeriodAppointments(String id, PageableRequest pageableRequest, DatePeriodRequest request) {
    if (!hasPermissionToManageResource(id)) {
      throw new ForbiddenException();
    }

    Pageable pageable = PageRequest.of(
        pageableRequest.page(),
        pageableRequest.size(),
        Sort.by("scheduledTo").descending()
    );

    LocalDateTime startDateTime = request.startDateTime();
    LocalDateTime finishDateTime = request.finishDateTime();

    Page<GetAppointmentResponse> doctorPage = appointmentRepository.findAllBetweenDatePeriod(id, startDateTime,
            finishDateTime, pageable)
        .map(GetAppointmentResponse::from);

    return PageResponse.from(doctorPage);
  }

  @AuthRequired
  @RoleRestricted("PATIENT")
  @Transactional
  public CreateAppointmentResponse createAppointment(CreateAppointmentRequest request) {

    String patientId = UserContext.getUserId();

    doctorsClient.isDoctorValid(request.doctorId());

    LocalDateTime date = request.dateTime();

    if (appointmentRepository.existsConflictByPatientInDateTime(patientId, date))
      throw new ConflictException("The patient already has other appointment that day");

    LocalDateTime oneHourLater = request.dateTime().plusMinutes(59);
    LocalDateTime oneHourbefore = request.dateTime().minusMinutes(59);

    if (appointmentRepository.existsConflictByDoctorInDateTime(request.doctorId(), oneHourbefore, oneHourLater))
      throw new ConflictException("The doctor already has other appointment near that time");

    Appointment appointment = Appointment.fromCreationRequest(request, patientId);

    return new CreateAppointmentResponse(appointmentRepository.save(appointment));
  }

  @AuthRequired
  @Transactional
  public void cancelAppointment(String id) {
    log.info("Cancelling appointment with id: {}", id);
    
    Appointment appointment = appointmentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Messages.APPOINTMENT_NOT_FOUND));

    if (!hasPermissionToManageResource(appointment.getDoctorId(), appointment.getPatientId())) {
      throw new ForbiddenException();
    }
    
    String reason = "";
    List<UserRole> roles = UserContext.getUserRoles();
    
    Optional<UserRole> patientRole = roles.stream()
        .filter(role -> Objects.equals(role.role(), "PATIENT"))
        .findAny();

    Optional<UserRole> doctorRole = roles.stream()
        .filter(role -> Objects.equals(role.role(), "DOCTOR"))
        .findAny();
    
    if (patientRole.isPresent()) {
      reason = "Desistência";
    } else if (doctorRole.isPresent()) {
      reason = "Cancelada pelo médico";
    } else {
      reason = "Cancelada pelo administrador";
    }

    appointment.setCancelled(true);
    appointment.setObservation(reason);

    appointmentRepository.save(appointment);

    log.info("Appointment with id: {} cancelled successfully, cause: {}", id, reason);
  }

  @Transactional
  public void deletePatientAppointments(String id) {
    log.info("Deleting appointments for Patient with id: {}", id);

    appointmentRepository.deleteAllByPatientId(id);

    log.info("Appointments for Patient with id: {} deleted successfully.", id);
  }

  @Transactional
  public void deleteDoctorAppointments(String id) {
    log.info("Deleting appointments for Doctor with id: {}", id);

    appointmentRepository.deleteAllByDoctorId(id);

    log.info("Appointments for Doctor with id: {} deleted successfully.", id);
  }

  private boolean hasPermissionToManageResource(String resourceOwnerId) {
    String requesterId = UserContext.getUserId();
    List<UserRole> requesterRoles = UserContext.getUserRoles();

    boolean isResourceOwner = resourceOwnerId.equals(requesterId) || requesterRoles.stream().anyMatch(
        role -> role.referencedEntityId() != null && role.referencedEntityId().equals(resourceOwnerId)
    );

    boolean isAdminUser = requesterRoles.stream()
        .anyMatch(role -> Objects.equals(role.role(), "ADMIN"));

    return isResourceOwner || isAdminUser;
  }

  private boolean hasPermissionToManageResource(String... resourceOwnersIds) {

    String requesterId = UserContext.getUserId();
    List<UserRole> requesterRoles = UserContext.getUserRoles();

    boolean isResourceOwner = Arrays.asList(resourceOwnersIds).contains(requesterId);

    boolean isViaRoleOwner = Stream.of(resourceOwnersIds).anyMatch(resourceOwnerId ->
        requesterRoles.stream().anyMatch(
            role -> role.referencedEntityId() != null && role.referencedEntityId().equals(resourceOwnerId)
        )
    );

    boolean isAdminUser = requesterRoles.stream()
        .anyMatch(role -> Objects.equals(role.role(), "ADMIN"));

    return isResourceOwner || isAdminUser || isViaRoleOwner;
  }

}
