package com.ifba.clinic.access.models.response;

import com.ifba.clinic.access.entities.User;
import com.ifba.clinic.access.entities.UserTrait;
import com.ifba.clinic.access.entities.enums.EnumIntentStatus;
import java.util.List;

public record CurrentUserResponse(
    String id,
    String email,
    List<UserRoleResponse> roles,
    List<String> traits,
    List<ProfileIntentResponse> pendingIntents
) {
  public CurrentUserResponse(
      User user
  ) {
    this(
        user.getId(),
        user.getEmail(),
        user.getRoles().stream().map(UserRoleResponse::new).toList(),
        user.getTraits().stream().map(UserTrait::getTrait).toList(),
        user.getProfileIntents().stream()
            .filter(intent -> intent.getStatus() == EnumIntentStatus.PENDING || intent.getStatus() == EnumIntentStatus.IMPLICIT)
            .map(ProfileIntentResponse::new)
            .toList()
    );
  }
}
