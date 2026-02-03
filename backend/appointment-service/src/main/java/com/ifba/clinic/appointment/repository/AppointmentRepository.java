package com.ifba.clinic.appointment.repository;

import com.ifba.clinic.appointment.entities.Appointment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String>, AppointmentCustomRepository {

  @Query(
      "SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctorId = :doctorId " +
      "AND a.scheduledTo > :startDateTime AND a.scheduledTo < :finishDateTime"
  )
  boolean existsConflictByDoctorInDateTime(
      @Param("doctorId") String doctorId,
      @Param("startDateTime") LocalDateTime startDateTime,
      @Param("finishDateTime") LocalDateTime finishDateTime
  );

  @Query(
      "SELECT COUNT(a) > 0 FROM Appointment a WHERE a.patientId = :patientId " +
      "AND CAST(a.scheduledTo AS date) = CAST(:date AS date) " +
      "AND NOT a.cancelled"
  )
  boolean existsConflictByPatientInDateTime(
      @Param("patientId") String patientId,
      @Param("date") LocalDateTime date
  );

  @Query("""
    SELECT a FROM Appointment a
    WHERE a.scheduledTo >= COALESCE(:startDateTime, a.scheduledTo)
    AND a.scheduledTo <= COALESCE(:finishDateTime, a.scheduledTo)
    AND a.patientId = :patientId
    ORDER BY a.scheduledTo DESC
  """)
  Page<Appointment> findAllByPatientBetweenDatePeriod(
      @Param("patientId") String patientId,
      @Param("startDateTime") LocalDateTime startDateTime,
      @Param("finishDateTime") LocalDateTime finishDateTime,
      Pageable pageable
  );

  @Query("""
    SELECT a FROM Appointment a
    WHERE a.scheduledTo >= COALESCE(:startDateTime, a.scheduledTo)
    AND a.scheduledTo <= COALESCE(:finishDateTime, a.scheduledTo)
    AND a.doctorId = :doctorId
    ORDER BY a.scheduledTo DESC
  """)
  Page<Appointment> findAllByDoctorBetweenDatePeriod(
      @Param("doctorId") String doctorId,
      @Param("startDateTime") LocalDateTime startDateTime,
      @Param("finishDateTime") LocalDateTime finishDateTime,
      Pageable pageable
  );

  void deleteAllByDoctorId(String doctorId);
  void deleteAllByPatientId(String patientId);

}
