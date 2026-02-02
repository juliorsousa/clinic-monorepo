package com.ifba.appointment.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ifba.appointment.entities.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.idDoctor = :idDoctor " +
           "AND a.status IN ('CONFIRMED', 'ONGOING', 'PENDING') " +
           "AND a.dateTime > :inicioIntervalo AND a.dateTime < :fimIntervalo")
    boolean existsConflictByDoctorInDateTime(String idDoctor, LocalDateTime inicioIntervalo, LocalDateTime fimIntervalo);


    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.idPatient = :idPatient " +
           "AND CAST(a.dateTime AS date) = CAST(:data AS date) " +
           "AND a.status != 'CANCELLED'")
    boolean existsConflictByPatientInDateTime(String idPatient, LocalDateTime data);

    Page<Appointment> findAllByIdPatient(Pageable pageable, String idPatient);

    Page<Appointment> findAllByIdDoctor(Pageable pageable, String idDoctor);

    @Query("SELECT a FROM Appointment a WHERE a.dateTime >= :start " +
       "AND a.dateTime <= :finish " +
       "AND a.idDoctor = :doctor " +
       "ORDER BY a.dateTime ASC")
    Page<Appointment> findAllBetweenDatePeriod(Pageable pageable, 
       @Param("start") LocalDateTime startDateTime, 
       @Param("finish") LocalDateTime finishDateTime,
       @Param("doctor") String idDoctor);
}
