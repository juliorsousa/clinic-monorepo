package com.ifba.clinic.access.security.components.aspects;

import com.ifba.clinic.access.exceptions.UnauthorizedException;
import com.ifba.clinic.access.security.annotations.AuthRequired;
import com.ifba.clinic.access.security.annotations.RoleRestricted;
import com.ifba.clinic.access.security.annotations.TraitRestricted;
import com.ifba.clinic.access.security.components.UserGrantsEvaluator;
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

  private final UserGrantsEvaluator userGrantsEvaluator;

  @Before("@annotation(authRequired)")
  public void isAuthenticated(JoinPoint joinPoint, AuthRequired authRequired) {
    if (!userGrantsEvaluator.hasAuthentication()) {
      throw new UnauthorizedException("You must be authenticated to access this resource.");
    }
  }

  @Before("@annotation(roleRestricted)")
  public void hasRole(JoinPoint joinPoint, RoleRestricted roleRestricted) {
    if (!userGrantsEvaluator.hasAuthentication()) {
      throw new UnauthorizedException("You must be authenticated to access this resource.");
    }

    if (!userGrantsEvaluator.hasRole(roleRestricted.value())) {
      throw new UnauthorizedException("You do not have the required role to access this resource.");
    }
  }

  @Before("@annotation(traitRestricted)")
  public void checkTraitRestriction(JoinPoint joinPoint, TraitRestricted traitRestricted) {
    if (!userGrantsEvaluator.hasAuthentication()) {
      throw new UnauthorizedException("You must be authenticated to access this resource.");
    }

    boolean hasTrait = userGrantsEvaluator.checkTrait(
        traitRestricted.policy(),
        traitRestricted.value()
    );

    if (!hasTrait) {
      log.warn("Access denied for method: {}. Trait restriction not met: {} with policy {}",
          joinPoint.getSignature().getName(),
          traitRestricted.value(),
          traitRestricted.policy()
      );

      throw new UnauthorizedException("You do not meet the required traits to access this resource.");
    }
  }

}