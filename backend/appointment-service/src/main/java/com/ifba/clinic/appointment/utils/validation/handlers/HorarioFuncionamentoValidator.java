package com.ifba.clinic.appointment.utils.validation.handlers;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import com.ifba.clinic.appointment.utils.validation.annotations.HorarioFuncionamento;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HorarioFuncionamentoValidator implements ConstraintValidator<HorarioFuncionamento, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime data, ConstraintValidatorContext context) {
        if (data == null) return false;

        boolean noFuturo = data.isAfter(LocalDateTime.now().plusMinutes(30));
        boolean diaUtil = !data.getDayOfWeek().equals(DayOfWeek.SUNDAY);
        boolean horarioValido = data.getHour() >= 7 && data.getHour() < 18;

        return noFuturo && diaUtil && horarioValido;
    }
}
