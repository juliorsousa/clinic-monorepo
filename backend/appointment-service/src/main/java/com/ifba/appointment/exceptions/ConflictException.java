package com.ifba.appointment.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import static com.ifba.appointment.utils.Messages.GENERIC_CONFLICT;;

@ResponseStatus(code = org.springframework.http.HttpStatus.CONFLICT, reason = GENERIC_CONFLICT)
public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }

  public ConflictException() {
    super(GENERIC_CONFLICT);
  }
}
