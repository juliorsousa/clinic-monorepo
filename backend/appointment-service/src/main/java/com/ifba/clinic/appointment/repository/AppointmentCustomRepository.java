package com.ifba.clinic.appointment.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AppointmentCustomRepository {

  List<LocalDate> findAvailableDatesForDoctors(List<String> doctorIds);

  Map<String, List<LocalDateTime>> findAvailableHoursForDoctorsOnDate(
      List<String> doctorIds, LocalDate date
  );
}
