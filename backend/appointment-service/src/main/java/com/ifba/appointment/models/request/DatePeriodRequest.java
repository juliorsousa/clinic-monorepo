package com.ifba.appointment.models.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ifba.appointment.utils.Messages;

import jakarta.validation.constraints.NotNull;

public record DatePeriodRequest(

    @NotNull(message = Messages.DATE_TIME_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime startDateTime,

    @NotNull(message = Messages.DATE_TIME_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime finishDateTime
    
) {}
