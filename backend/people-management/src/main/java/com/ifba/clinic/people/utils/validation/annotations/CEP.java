package com.ifba.clinic.people.utils.validation.annotations;

import com.ifba.clinic.people.utils.Messages;
import com.ifba.clinic.people.utils.validation.handlers.CEPValidationHandler;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CEPValidationHandler.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CEP {

  String message() default Messages.ZIP_CODE_INVALID;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
