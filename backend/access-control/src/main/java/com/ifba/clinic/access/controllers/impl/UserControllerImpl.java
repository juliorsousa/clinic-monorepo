package com.ifba.clinic.access.controllers.impl;

import com.ifba.clinic.access.controllers.UserController;
import com.ifba.clinic.access.models.response.CurrentUserResponse;
import com.ifba.clinic.access.services.UserService;
import lombok.RequiredArgsConstructor;
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
