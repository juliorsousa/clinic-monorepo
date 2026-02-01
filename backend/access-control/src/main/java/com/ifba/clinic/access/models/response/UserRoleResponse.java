package com.ifba.clinic.access.models.response;

import com.ifba.clinic.access.entities.UserRole;
import com.ifba.clinic.access.entities.enums.EnumRole;

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
