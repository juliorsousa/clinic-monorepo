package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.User;
import com.ifba.clinic.people.entities.UserTrait;
import java.util.List;

public record CurrentUserResponse(
    String id,
    String email,
    List<UserRoleResponse> roles,
    List<String> traits
) {
  public CurrentUserResponse(
      User user
  ) {
    this(
        user.getId(),
        user.getEmail(),
        user.getRoles().stream().map(UserRoleResponse::new).toList(),
        user.getTraits().stream().map(UserTrait::getTrait).toList()
    );
  }
}
