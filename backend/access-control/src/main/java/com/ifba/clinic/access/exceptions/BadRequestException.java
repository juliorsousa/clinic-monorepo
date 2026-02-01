package com.ifba.clinic.access.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.ifba.clinic.access.utils.Messages.GENERIC_BAD_REQUEST;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = GENERIC_BAD_REQUEST)
public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException() {
    super(GENERIC_BAD_REQUEST);
  }
}
