package com.ifba.clinic.people.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.ifba.clinic.people.utils.Messages.GENERIC_NOT_FOUND;
import static com.ifba.clinic.people.utils.Messages.GENERIC_NO_CONTENT;

@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = GENERIC_NO_CONTENT)
public class NoContentException extends RuntimeException {
  public NoContentException(String message) {
    super(message);
  }

  public NoContentException() {
    super(GENERIC_NO_CONTENT);
  }
}
