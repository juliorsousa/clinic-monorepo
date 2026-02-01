package com.ifba.clinic.access.security.filter;

import com.ifba.clinic.access.security.services.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

  private final AuthenticationService authenticationService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String token = extractToken(request);

    if (token != null && !token.isBlank()) {
      SecurityContextHolder
          .getContext()
          .setAuthentication(authenticationService.getAuthentication(token));
    }

    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }

    return null;
  }

}
