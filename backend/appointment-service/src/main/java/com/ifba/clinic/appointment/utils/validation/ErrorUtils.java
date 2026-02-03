package com.ifba.clinic.appointment.utils.validation;

import com.ifba.clinic.appointment.models.error.ValidationError;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

public class ErrorUtils {
  public static <T> ValidationError from(Set<ConstraintViolation<T>> violations) {
    var errors = violations.stream()
        .collect(
            java.util.stream.Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            )
        );

    return new ValidationError(
        "VALIDATION_FAILED",
        errors
    );
  }
}
