package com.ifba.clinic.people.controllers.impl;

import com.ifba.clinic.people.controllers.UserController;
import com.ifba.clinic.people.entities.enums.EnumRole;
import com.ifba.clinic.people.models.requests.CreateUserRequest;
import com.ifba.clinic.people.models.response.CreateUserResponse;
import com.ifba.clinic.people.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

  private final UserService userService;

  public ResponseEntity<CreateUserResponse> createUser(
      CreateUserRequest request
  ) {
    var created = userService.createUser(request, EnumRole.GUEST);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(created);
  }
}
