package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.User;

public record CreateUserResponse(
    String id,
    String email
) {
  public CreateUserResponse(User user) {
    this(
        user.getId(),
        user.getEmail()
    );
  }
}
