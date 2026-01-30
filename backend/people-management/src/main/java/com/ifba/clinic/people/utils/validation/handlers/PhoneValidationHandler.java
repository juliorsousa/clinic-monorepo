package com.ifba.clinic.people.utils.validation.handlers;

import com.ifba.clinic.people.utils.validation.annotations.Phone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidationHandler implements ConstraintValidator<Phone, String> {

  private static final String PHONE_REGEX = "^(\\(?\\d{2}\\)?\\s?)?(9?\\d{4}-?\\d{4})$";

  @Override
  public boolean isValid(String phone, ConstraintValidatorContext context) {
    if (phone == null || phone.isBlank()) {
      return true;
    }

    return phone.matches(PHONE_REGEX);
  }
}