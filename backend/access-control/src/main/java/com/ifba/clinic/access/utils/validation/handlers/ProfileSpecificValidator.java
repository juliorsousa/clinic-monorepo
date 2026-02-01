package com.ifba.clinic.access.utils.validation.handlers;

import com.ifba.clinic.access.models.requests.profiles.ProfileIntentRequest;
import com.ifba.clinic.access.utils.validation.annotations.ValidProfileSpecific;
import com.ifba.clinic.access.utils.validation.groups.DoctorGroup;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfileSpecificValidator
    implements ConstraintValidator<ValidProfileSpecific, ProfileIntentRequest> {

  @Override
  public boolean isValid(ProfileIntentRequest request, ConstraintValidatorContext context) {
    if (request == null) return true;

    boolean isDoctor =
        "DOCTOR".equalsIgnoreCase(request.profile().value());

    if (isDoctor) {
      Validator validator =
          Validation.buildDefaultValidatorFactory().getValidator();

      return validator.validate(request.specific(), DoctorGroup.class).isEmpty();
    }


    return true;
  }
}
