package com.ifba.clinic.appointment.security.components;

import com.ifba.clinic.appointment.models.response.UserRole;
import com.ifba.clinic.appointment.security.models.UserContext;
import com.ifba.clinic.appointment.exceptions.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class GatewayHeaderInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
    String userId = request.getHeader("X-User-Id");
    String userEmail = request.getHeader("X-User-Email");

    if (userId == null) {
      throw new ForbiddenException("Acesso direto não permitido");
    }

    List<UserRole> userRoles = new ArrayList<>();
    List<String> userTraits = new ArrayList<>();

    Enumeration<String> roleHeaders = request.getHeaders("X-User-Role");

    while (roleHeaders.hasMoreElements()) {
      String headerContent = roleHeaders.nextElement();

      String[] parts = headerContent.split(";");

      String role = parts[0];
      String referencedEntityId = parts.length > 1 ? parts[1] : null;

      userRoles.add(new UserRole(role, referencedEntityId));
    }

    Enumeration<String> traitHeaders = request.getHeaders("X-User-Trait");

    while (traitHeaders.hasMoreElements()) {
      String trait = traitHeaders.nextElement();

      userTraits.add(trait);
    }

    UserContext.setContext(userId, userEmail, userRoles, userTraits);
    return true;
  }

  @Override
  public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
    UserContext.clear();
  }

}
