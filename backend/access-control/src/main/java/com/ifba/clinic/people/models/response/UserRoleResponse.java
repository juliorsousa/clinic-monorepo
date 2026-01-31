package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.UserRole;
import com.ifba.clinic.people.entities.enums.EnumRole;

public record UserRoleResponse(
    String id,
    EnumRole role,
    String referencedEntityId
) {
  public UserRoleResponse(
      UserRole role
  ) {
    this(role.getId(), role.getRole(), role.getReferencedEntityId());
  }
}
