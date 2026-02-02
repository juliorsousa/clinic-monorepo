package com.ifba.clinic.people.security.components;

import com.ifba.clinic.people.models.response.UserRole;
import com.ifba.clinic.people.security.models.UserContext;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationComponent {

  public boolean hasPermissionToManageResource(String resourceOwnerId) {
    String requesterId = UserContext.getUserId();
    List<UserRole> requesterRoles = UserContext.getUserRoles();

    boolean isResourceOwner = resourceOwnerId.equals(requesterId);
    boolean isAdminUser = requesterRoles.stream()
        .anyMatch(role -> Objects.equals(role.role(), "ADMIN"));

    return isResourceOwner || isAdminUser;
  }

}
