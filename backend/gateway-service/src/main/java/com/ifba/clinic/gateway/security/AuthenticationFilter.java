package com.ifba.clinic.gateway.security;

import com.ifba.clinic.gateway.exceptions.UnauthorizedException;
import com.ifba.clinic.gateway.model.ValidateUserResponse;
import java.util.Objects;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final WebClient.Builder webClientBuilder;

  public AuthenticationFilter(WebClient.Builder webClientBuilder) {
    super(Config.class);
    this.webClientBuilder = webClientBuilder;
  }

  public static class Config {
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
        throw new UnauthorizedException("Authorization header is missing");
      }

      String authHeader = Objects.requireNonNull(
          exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)
      ).get(0);

      String[] parts = authHeader.split(" ");

      if (parts.length != 2 || !"Bearer".equals(parts[0])) {
        throw new UnauthorizedException("Invalid Token Format");
      }

      return webClientBuilder.build()
          .get()
          .uri("lb://access-control/auth/validate")
          .header(HttpHeaders.AUTHORIZATION, authHeader)
          .retrieve()
          .bodyToMono(ValidateUserResponse.class)
          .flatMap(user -> {
            ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Id", user.id())
                .header("X-User-Email", user.email())
                // TODO: add roles and traits
                .build();
            return chain.filter(exchange.mutate().request(request).build());
          })
          .onErrorResume(err -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token")));
    };
  }
}
