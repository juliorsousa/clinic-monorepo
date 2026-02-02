package com.ifba.appointment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.ifba.appointment.utils.Messages.GENERIC_FORBIDDEN;;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = GENERIC_FORBIDDEN)
public class ForbiddenException extends RuntimeException {
  public ForbiddenException(String message) {
    super(message);
  }

  public ForbiddenException() {
    super(GENERIC_FORBIDDEN);
  }
}
