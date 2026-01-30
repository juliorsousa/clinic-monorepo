package com.ifba.clinic.people.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import static com.ifba.clinic.people.utils.Messages.GENERIC_CONFLICT;

@ResponseStatus(code = org.springframework.http.HttpStatus.CONFLICT, reason = GENERIC_CONFLICT)
public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException() {
    super(GENERIC_CONFLICT);
  }
}
