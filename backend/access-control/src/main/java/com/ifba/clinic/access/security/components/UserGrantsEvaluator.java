package com.ifba.clinic.access.security.components;

import com.ifba.clinic.access.entities.User;
import com.ifba.clinic.access.security.enums.TraitPolicy;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userGrantsEvaluator")
@Slf4j
public class UserGrantsEvaluator {

  public boolean hasAuthentication()  {
    Authentication auth = SecurityContextHolder
        .getContext()
        .getAuthentication();

    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    User user = auth.getPrincipal() instanceof User
        ? (User) auth.getPrincipal()
        : null;

    return user != null;
  }

  public boolean hasRole(String role)  {
    Authentication auth = SecurityContextHolder
        .getContext()
        .getAuthentication();

    if (auth == null) {
      return false;
    }

    return auth.getAuthorities()
        .stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
  }

  public boolean checkTrait(TraitPolicy policy, String trait)  {
    Authentication auth = SecurityContextHolder
        .getContext()
        .getAuthentication();

    if (auth == null) {
      return false;
    }

    Predicate<GrantedAuthority> traitAppearencePredicate = grantedAuthority -> grantedAuthority.getAuthority().equals("TRAIT:" + trait);

    return auth.getAuthorities()
        .stream()
        .anyMatch(traitAppearencePredicate) == (policy == TraitPolicy.HAVE);
  }

}
