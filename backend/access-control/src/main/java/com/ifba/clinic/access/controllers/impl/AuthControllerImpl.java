package com.ifba.clinic.access.controllers.impl;

import com.ifba.clinic.access.controllers.AuthController;
import com.ifba.clinic.access.entities.enums.EnumRole;
import com.ifba.clinic.access.models.requests.CreateUserRequest;
import com.ifba.clinic.access.models.requests.LoginUserRequest;
import com.ifba.clinic.access.models.requests.ChangePasswordRequest;
import com.ifba.clinic.access.models.response.CreateUserResponse;
import com.ifba.clinic.access.models.response.TokenResponse;
import com.ifba.clinic.access.models.response.ValidateUserResponse;
import com.ifba.clinic.access.security.services.AuthenticationService;
import com.ifba.clinic.access.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

  private final AuthenticationService authenticationService;
  private final UserService userService;

  public ResponseEntity<CreateUserResponse> registerUser(
      CreateUserRequest request
  ) {
    var created = userService.createUser(request, EnumRole.GUEST, "PENDING_ONBOARDING");

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(created);
  }

  public ResponseEntity<TokenResponse> loginUser(LoginUserRequest request) {
    return ResponseEntity
        .ok()
        .body(
            authenticationService.authenticateUser(request)
        );
  }

  public ResponseEntity<String> changePassword(ChangePasswordRequest request) {
    userService.changePassword(request);

    return ResponseEntity
        .ok()
        .body("Password changed successfully.");
  }

  @Override
  public ResponseEntity<ValidateUserResponse> validateToken() {

    ValidateUserResponse response = userService.getAuthenticatedUser();

    return ResponseEntity.ok(response);
  }
}
