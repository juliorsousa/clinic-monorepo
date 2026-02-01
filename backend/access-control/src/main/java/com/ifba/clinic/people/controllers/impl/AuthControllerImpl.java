package com.ifba.clinic.people.controllers.impl;

import com.ifba.clinic.people.controllers.AuthController;
import com.ifba.clinic.people.entities.enums.EnumRole;
import com.ifba.clinic.people.models.requests.CreateUserRequest;
import com.ifba.clinic.people.models.requests.LoginUserRequest;
import com.ifba.clinic.people.models.requests.ChangePasswordRequest;
import com.ifba.clinic.people.models.response.CreateUserResponse;
import com.ifba.clinic.people.models.response.TokenResponse;
import com.ifba.clinic.people.security.services.AuthenticationService;
import com.ifba.clinic.people.services.UserService;
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
}
