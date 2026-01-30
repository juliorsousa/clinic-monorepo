package com.ifba.clinic.people.handlers;

import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.exceptions.NotFoundException;
import com.ifba.clinic.people.models.error.MessagedError;
import com.ifba.clinic.people.models.error.ValidationError;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<MessagedError> handleConflictException(ConflictException exception) {
    String message = exception.getMessage() != null
        ? exception.getMessage()
        : "The request could not be completed due to a conflict with the current state of the resource.";

    return ResponseEntity
        .status(409)
        .body(new MessagedError(message));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<MessagedError> handleNotFoundException(NotFoundException exception) {
    String message = exception.getMessage() != null
        ? exception.getMessage()
        : "The requested resource was not found.";

    return ResponseEntity
        .status(404)
        .body(new MessagedError(message));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
    Map<String, String> errors = exception.getBindingResult().getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value"
        ));

    ValidationError validationErrorResponse = new ValidationError(
        "Validation failed for the request parameters.",
        errors
    );

    log.warn("Validation errors : {} ; Parameters : {}", errors.values(), exception.getBindingResult().getTarget());

    return ResponseEntity
        .status(400)
        .body(validationErrorResponse);
  }

  // Handle generic exceptions
  @ExceptionHandler(Exception.class)
  public ResponseEntity<MessagedError> handleGenericException(Exception exception) {
    log.error("An unexpected error occurred: ", exception);

    return ResponseEntity
        .status(500)
        .body(new MessagedError("An unexpected error occurred. Please try again later."));
  }
}