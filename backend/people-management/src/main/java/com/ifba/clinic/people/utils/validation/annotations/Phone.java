package com.ifba.clinic.people.utils.validation.annotations;

import com.ifba.clinic.people.utils.Messages;
import com.ifba.clinic.people.utils.validation.handlers.PhoneValidationHandler;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidationHandler.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {

  String message() default Messages.PHONE_INVALID;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
