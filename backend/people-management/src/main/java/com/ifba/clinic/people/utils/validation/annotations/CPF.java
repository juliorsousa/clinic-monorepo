package com.ifba.clinic.people.utils.validation.annotations;

import com.ifba.clinic.people.utils.Messages;
import com.ifba.clinic.people.utils.validation.handlers.CPFValidationHandler;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CPFValidationHandler.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CPF {

  String message() default Messages.DOCUMENT_INVALID;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
