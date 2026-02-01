package com.ifba.clinic.access.utils.validation.annotations;

import com.ifba.clinic.access.utils.validation.handlers.ProfileSpecificValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProfileSpecificValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidProfileSpecific {

  String message() default "Dados específicos são obrigatórios para médicos.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
