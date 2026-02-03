package com.ifba.clinic.appointment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.ifba.clinic.appointment.utils.Messages.GENERIC_NOT_FOUND;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = GENERIC_NOT_FOUND)
public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException() {
    super(GENERIC_NOT_FOUND);
  }
}
