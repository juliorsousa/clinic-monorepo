package com.ifba.clinic.access.security.services;

import com.ifba.clinic.access.entities.User;
import com.ifba.clinic.access.exceptions.BadRequestException;
import com.ifba.clinic.access.models.requests.LoginUserRequest;
import com.ifba.clinic.access.models.response.TokenResponse;
import com.ifba.clinic.access.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.ifba.clinic.access.utils.Messages.AUTH_INVALID_CREDENTIALS;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthenticationService {

  @Value("${security.jwt.secret}")
  private String jwtSecret;

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  public TokenResponse authenticateUser(
      LoginUserRequest request
  ) {
    String email = request.email();
    String password = request.password();

    log.info("Authenticating user with email: {}", email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BadRequestException(AUTH_INVALID_CREDENTIALS));

    if (passwordEncoder.matches(password, user.getPassword())) {
      String token = this.generateToken(user);

      return new TokenResponse(token);
    }

    throw new BadRequestException(AUTH_INVALID_CREDENTIALS);
  }

  public UsernamePasswordAuthenticationToken getAuthentication(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(this.getJWTSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    String userId = claims.get("usr", String.class);

    User user = userRepository.findById(userId)
        .orElse(null);

    if (user != null) {
      List<SimpleGrantedAuthority> roles = user.getRoles()
          .stream()
          .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
          .toList();

      List<SimpleGrantedAuthority> traits = user.getTraits()
          .stream()
          .map(trait -> new SimpleGrantedAuthority("TRAIT:" + trait.getTrait()))
          .toList();

      return new UsernamePasswordAuthenticationToken(
          user,
          null,
          new ArrayList<SimpleGrantedAuthority>() {{
            addAll(roles);
            addAll(traits);
          }}
      );
    }

    return null;
  }

  public String generateToken(User user) {
    Instant expiration = Instant.now().plusSeconds(3600);

    SecretKey key = this.getJWTSigningKey();

    return Jwts.builder()
        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
        .claim("usr", user.getId())
        .setExpiration(Date.from(expiration))
        .signWith(key)
        .compact();
  }

  private SecretKey getJWTSigningKey() {
    return Keys.hmacShaKeyFor(this.jwtSecret.getBytes(StandardCharsets.UTF_8));
  }
}