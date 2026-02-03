package com.ifba.clinic.appointment.services;

import com.ifba.clinic.appointment.entities.Appointment;
import com.ifba.clinic.appointment.entities.enums.AppointmentStatus;
import com.ifba.clinic.appointment.exceptions.ConflictException;
import com.ifba.clinic.appointment.exceptions.ForbiddenException;
import com.ifba.clinic.appointment.exceptions.NotFoundException;
import com.ifba.clinic.appointment.feign.DoctorsClient;
import com.ifba.clinic.appointment.feign.models.SummarizedDoctorResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import static com.ifba.clinic.appointment.utils.Messages.DOCTOR_NOT_AVAILABLE;
import static com.ifba.clinic.appointment.utils.Messages.DOCTOR_SPECIALTY_MISMATCH;
import static com.ifba.clinic.appointment.utils.Messages.NO_DOCTORS_FOR_SPECIALTY;
import static com.ifba.clinic.appointment.utils.Messages.PATIENT_ALREADY_HAS_APPOINTMENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final DoctorsClient doctorsClient;

//  @AuthRequired
  public List<LocalDate> getAvailableDatesForDoctors(List<String> doctorIds) {
    return appointmentRepository.findAvailableDatesForDoctors(doctorIds);
  }

//  @AuthRequired
  public Map<String, List<LocalDateTime>> getAvailableHoursForDoctorsOnDate(List<String> doctorIds, LocalDate date) {
    return appointmentRepository.findAvailableHoursForDoctorsOnDate(doctorIds, date);
  }

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
    String patientId = UserContext.getUserRoles().stream()
        .filter(role -> Objects.equals(role.role(), "PATIENT"))
        .findFirst()
        .orElseThrow(ForbiddenException::new)
        .referencedEntityId();

    LocalDateTime date = request.dateTime();

    if (appointmentRepository.existsConflictByPatientInDateTime(patientId, date)) {
      throw new ConflictException(PATIENT_ALREADY_HAS_APPOINTMENT);
    }

    List<String> doctorIds =
        doctorsClient.getDoctorsSummaryBySpeciality(request.specialty())
            .stream()
            .map(SummarizedDoctorResponse::id)
            .toList();

    if (doctorIds.isEmpty()) {
      throw new ConflictException(NO_DOCTORS_FOR_SPECIALTY);
    }

    Map<String, List<LocalDateTime>> availableHoursMap =
        appointmentRepository.findAvailableHoursForDoctorsOnDate(
            doctorIds,
            date.toLocalDate()
        );

    String selectedDoctorId;

    if (request.doctorId() == null) {
      selectedDoctorId = availableHoursMap.entrySet().stream()
          .filter(e -> e.getValue().contains(date))
          .map(Map.Entry::getKey)
          .findAny()
          .orElseThrow(() -> new ConflictException(DOCTOR_NOT_AVAILABLE));
    } else {
      if (!doctorIds.contains(request.doctorId())) {
        throw new ConflictException(DOCTOR_SPECIALTY_MISMATCH);
      }

      if (!availableHoursMap
          .getOrDefault(request.doctorId(), List.of())
          .contains(date)) {

        throw new ConflictException(DOCTOR_NOT_AVAILABLE);
      }

      selectedDoctorId = request.doctorId();
    }

    Appointment appointment =
        Appointment.fromCreationRequest(request, patientId, selectedDoctorId);

    return new CreateAppointmentResponse(
        appointmentRepository.save(appointment)
    );
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

    if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
      throw new ConflictException(
          appointment.getStatus() == AppointmentStatus.CONFIRMED || appointment.getStatus() == AppointmentStatus.ONGOING
              ? Messages.CANT_CANCEL_APPOINTMENT
              : Messages.CANT_CANCEL_PAST_APPOINTMENT
      );
    }

    String reason;
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
