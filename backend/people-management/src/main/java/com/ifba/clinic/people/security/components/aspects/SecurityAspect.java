package com.ifba.clinic.people.security.components.aspects;

import com.ifba.clinic.people.exceptions.ForbiddenException;
import com.ifba.clinic.people.exceptions.UnauthorizedException;
import com.ifba.clinic.people.security.annotations.AuthRequired;
import com.ifba.clinic.people.security.annotations.RoleRestricted;
import com.ifba.clinic.people.security.models.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityAspect {

  @Before("@annotation(authRequired)")
  public void isAuthenticated(JoinPoint joinPoint, AuthRequired authRequired) {
    if (!hasAuthentication()) {
      throw new UnauthorizedException();
    }
  }

  @Before("@annotation(roleRestricted)")
  public void hasRole(JoinPoint joinPoint, RoleRestricted roleRestricted) {
    if (!hasAuthentication()) {
      throw new UnauthorizedException();
    }

    if (!hasRole(roleRestricted.value())) {
      throw new ForbiddenException();
    }
  }

  private boolean hasAuthentication() {
    String userId = UserContext.getUserId();

    return (userId != null && !userId.isEmpty()) || UserContext.isSystemCall();
  }

  private boolean hasRole(String role) {
    if (UserContext.isSystemCall()) {
      return true;
    }

    return UserContext.getUserRoles()
        .stream()
        .anyMatch(userRole -> userRole.role().equals(role));
  }

}