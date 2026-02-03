package com.ifba.clinic.appointment.models.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ifba.clinic.appointment.utils.Messages;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

public record DatePeriodRequest(
    @DateTimeFormat()
    LocalDateTime startDateTime,

    @DateTimeFormat()
    LocalDateTime finishDateTime
) {

  public static DatePeriodRequest of(LocalDateTime startDateTime, LocalDateTime finishDateTime) {
    return new DatePeriodRequest(startDateTime, finishDateTime);
  }

  public boolean isValidPeriod() {
    return startDateTime.isBefore(finishDateTime) || startDateTime.isEqual(finishDateTime);
  }

}
