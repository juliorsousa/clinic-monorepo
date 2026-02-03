package com.ifba.clinic.appointment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.ifba.clinic.appointment.utils.Messages.GENERIC_UNAUTHORIZED;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = GENERIC_UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException(String message) {
    super(message);
  }

  public UnauthorizedException() {
    super(GENERIC_UNAUTHORIZED);
  }
}
