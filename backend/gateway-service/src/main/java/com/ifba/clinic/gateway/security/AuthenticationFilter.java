package com.ifba.clinic.gateway.security;

import com.ifba.clinic.gateway.exceptions.UnauthorizedException;
import com.ifba.clinic.gateway.model.ValidateUserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
      String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
      if (authHeader == null || authHeader.isBlank()) {
        return Mono.error(new UnauthorizedException("Authorization header is missing"));
      }

      String[] parts = authHeader.split(" ");

      if (parts.length != 2 || !"Bearer".equalsIgnoreCase(parts[0])) {
        return Mono.error(new UnauthorizedException("Invalid Token Format"));
      }

      return webClientBuilder.build()
          .get()
          .uri("lb://access-control/auth/validate")
          .header(HttpHeaders.AUTHORIZATION, authHeader)
          .retrieve()
          .bodyToMono(ValidateUserResponse.class)
          .flatMap(user -> {
            ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
                .header("X-User-Id", user.id())
                .header("X-User-Email", user.email());

            user.roles().forEach(role ->
                requestBuilder.header("X-User-Role", role.role() + ";" + role.referencedEntityId())
            );

            user.traits().forEach(trait -> requestBuilder.header("X-User-Trait", trait));

            return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
          })
          .onErrorResume(err -> {
            if (err instanceof WebClientResponseException.Unauthorized) {
              return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token"));
            } else if (err instanceof WebClientResponseException) {
              return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Auth Service Error"));
            } else {
              return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error"));
            }
          });
    };
  }
}
