package com.ifba.clinic.people.controllers.impl;

import com.ifba.clinic.people.controllers.UserController;
import com.ifba.clinic.people.models.response.CurrentUserResponse;
import com.ifba.clinic.people.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

  private final UserService userService;

  public ResponseEntity<CurrentUserResponse> getCurrentUser() {
    return ResponseEntity
        .ok()
        .body(
            new CurrentUserResponse(userService.getCurrentUser())
        );
  }

}
