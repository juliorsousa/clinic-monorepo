package com.ifba.gateway_service.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.ifba.gateway_service.exceptions.UnauthorizedException;
import com.ifba.gateway_service.model.ValidateUserResponse;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    
    private final WebClient.Builder webClientBuilder;

    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Verificar se o header de Authorization existe
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new UnauthorizedException("Authorization header is missing");

            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String[] parts = authHeader.split(" ");

            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                throw new UnauthorizedException("Invalid Token Format");
            }

            // 2. Chamar o microserviço de Access Control via WebClient (Não bloqueante)
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
                            .build();
                        return chain.filter(exchange.mutate().request(request).build());
                    })
                    .onErrorResume(err -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token")));
        };
    }
}
