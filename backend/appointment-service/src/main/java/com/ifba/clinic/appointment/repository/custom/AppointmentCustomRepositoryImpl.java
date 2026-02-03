package com.ifba.clinic.appointment.repository.custom;

import com.ifba.clinic.appointment.entities.Appointment;
import com.ifba.clinic.appointment.repository.AppointmentCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class AppointmentCustomRepositoryImpl implements AppointmentCustomRepository {

  @PersistenceContext
  private EntityManager entityManager;

  private static final int START_HOUR = 7;
  private static final int END_HOUR = 19;
  private static final int MIN_MINUTES_AHEAD = 30;
  private static final int MAX_DAYS_AHEAD = 30;

  @Override
  public List<LocalDate> findAvailableDatesForDoctors(List<String> doctorIds) {

    LocalDate today = LocalDate.now();
    LocalDate limit = today.plusDays(MAX_DAYS_AHEAD);

    List<LocalDate> availableDates = new ArrayList<>();

    for (LocalDate date = today; !date.isAfter(limit); date = date.plusDays(1)) {

      if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
        continue;
      }

      LocalDate finalDate = date;
      boolean anyDoctorAvailable =
          doctorIds.stream().anyMatch(id -> hasAnyAvailableHour(id, finalDate));

      if (anyDoctorAvailable) {
        availableDates.add(date);
      }
    }

    return availableDates;
  }

  @Override
  public Map<String, List<LocalDateTime>> findAvailableHoursForDoctorsOnDate(
      List<String> doctorIds, LocalDate date) {

    LocalDate today = LocalDate.now();
    LocalDate limit = today.plusDays(MAX_DAYS_AHEAD);

    if (date.isBefore(today) || date.isAfter(limit)) {
      throw new IllegalArgumentException(
          "Date must be between today and 30 days ahead"
      );
    }

    if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
      throw new IllegalArgumentException("Appointments are not allowed on Sundays");
    }

    Map<String, List<LocalDateTime>> result = new HashMap<>();

    for (String doctorId : doctorIds) {
      result.put(doctorId, getAvailableSlotsForDoctor(doctorId, date));
    }

    return result;
  }

  private boolean hasAnyAvailableHour(String doctorId, LocalDate date) {
    return !getAvailableSlotsForDoctor(doctorId, date).isEmpty();
  }

  private List<LocalDateTime> getAvailableSlotsForDoctor(
      String doctorId, LocalDate date) {

    List<Appointment> appointments = entityManager
        .createQuery(
            "SELECT a FROM Appointment a WHERE a.doctorId = :doctorId",
            Appointment.class)
        .setParameter("doctorId", doctorId)
        .getResultList();

    List<LocalDateTime> available = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();

    for (int hour = START_HOUR; hour < END_HOUR; hour++) {

      LocalDateTime slotStart = date.atTime(hour, 0);
      LocalDateTime slotEnd = slotStart.plusHours(1);

      if (slotStart.isBefore(now.plusMinutes(MIN_MINUTES_AHEAD))) {
        continue;
      }

      boolean conflicts = appointments.stream().anyMatch(a ->
          !a.isCancelled() &&
              a.getScheduledTo().isBefore(slotEnd) &&
              a.getScheduledTo().plusHours(1).isAfter(slotStart)
      );

      if (!conflicts) {
        available.add(slotStart);
      }
    }

    return available;
  }
}
