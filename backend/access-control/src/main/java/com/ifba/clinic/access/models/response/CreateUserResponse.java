package com.ifba.clinic.access.models.response;

import com.ifba.clinic.access.entities.User;

public record CreateUserResponse(
    String id,
    String email,

    TokenResponse token
) {
  public CreateUserResponse(
      User user
  ) {
    this(user.getId(), user.getEmail(), null);
  }

  public CreateUserResponse(User user, String token) {
    this(
        user.getId(),
        user.getEmail(),
        new TokenResponse(token)
    );
  }
}
