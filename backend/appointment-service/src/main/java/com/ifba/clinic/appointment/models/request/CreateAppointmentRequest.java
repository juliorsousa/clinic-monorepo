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

    @NotBlank(message = Messages.ID_DOCTOR_REQUIRED)
    String doctorId,

    @NotNull(message = Messages.DATE_TIME_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    @HorarioFuncionamento
    LocalDateTime dateTime

) {
}

