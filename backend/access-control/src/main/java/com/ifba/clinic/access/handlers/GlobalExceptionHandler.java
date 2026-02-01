package com.ifba.clinic.access.handlers;

import com.ifba.clinic.access.exceptions.BadRequestException;
import com.ifba.clinic.access.exceptions.ConflictException;
import com.ifba.clinic.access.exceptions.NoContentException;
import com.ifba.clinic.access.exceptions.NotFoundException;
import com.ifba.clinic.access.exceptions.UnauthorizedException;
import com.ifba.clinic.access.models.error.MessagedError;
import com.ifba.clinic.access.models.error.ValidationError;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<MessagedError> handleAuthorizationDeniedException() {
    return ResponseEntity
        .status(403)
        .body(new MessagedError("You do not have permission to access this resource."));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<MessagedError> handleBadRequestException(BadRequestException exception) {
    String message = exception.getMessage() != null
        ? exception.getMessage()
        : "The request could not be understood or was missing required parameters.";

    return ResponseEntity
        .status(400)
        .body(new MessagedError(message));
  }

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

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<MessagedError> handleUnauthorizedException(UnauthorizedException exception) {
    String message = exception.getMessage() != null
        ? exception.getMessage()
        : "Authentication is required and has failed or has not yet been provided.";

    return ResponseEntity
        .status(401)
        .body(new MessagedError(message));
  }

  @ExceptionHandler(NoContentException.class)
  public ResponseEntity<MessagedError> handleUnauthorizedException(NoContentException exception) {
    String message = exception.getMessage() != null
        ? exception.getMessage()
        : "The request was successful but there is no content to send for this request.";

    return ResponseEntity
        .status(204)
        .body(new MessagedError(message));
  }

  @ExceptionHandler(MalformedJwtException.class)
  public ResponseEntity<MessagedError> handleMalformedJwtException(MalformedJwtException exception) {
    String message = "Invalid authentication token provided.";

    log.error("Malformed JWT Exception: ", exception);

    return ResponseEntity
        .status(401)
        .body(new MessagedError(message));
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<MessagedError> handleMalformedJwtException(ExpiredJwtException exception) {
    String message = "Sua sessão expirou. Por favor, faça login novamente.";

    return ResponseEntity
        .status(401)
        .body(new MessagedError(message));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<MessagedError> handleNoResourceFoundException(NoResourceFoundException exception) {
    String message = "The requested resource was not found.";

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