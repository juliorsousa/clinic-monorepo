package com.ifba.clinic.appointment.models.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ifba.clinic.appointment.utils.Messages;
import com.ifba.clinic.appointment.utils.validation.annotations.HorarioFuncionamento;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateAppointmentRequest (

    @NotNull
    @NotBlank(message = "A especialidade da consulta não pode estar vazia.")
    String specialty,

    String doctorId,

    @NotNull(message = Messages.DATE_TIME_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @HorarioFuncionamento
    LocalDateTime dateTime

) {
}

