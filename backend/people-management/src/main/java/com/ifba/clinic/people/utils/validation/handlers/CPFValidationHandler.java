package com.ifba.clinic.people.utils.validation.handlers;

import com.ifba.clinic.people.utils.validation.annotations.CPF;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidationHandler  implements ConstraintValidator<CPF, String> {

  @Override
  public boolean isValid(String cpf, ConstraintValidatorContext context) {
    if (cpf == null || cpf.isBlank()) return true;

    cpf = cpf.replaceAll("\\D", "");

    if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
      return false;
    }

    return isValidDigits(cpf);
  }

  private boolean isValidDigits(String cpf) {
    int sum = 0;

    for (int i = 0; i < 9; i++) {
      sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
    }

    int firstDigit = 11 - (sum % 11);
    firstDigit = firstDigit > 9 ? 0 : firstDigit;

    sum = 0;
    for (int i = 0; i < 10; i++) {
      sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
    }

    int secondDigit = 11 - (sum % 11);
    secondDigit = secondDigit > 9 ? 0 : secondDigit;

    return firstDigit == Character.getNumericValue(cpf.charAt(9))
        && secondDigit == Character.getNumericValue(cpf.charAt(10));
  }
}