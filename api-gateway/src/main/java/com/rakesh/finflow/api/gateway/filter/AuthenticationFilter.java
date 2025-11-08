package com.rakesh.finflow.api.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final WebClient webClient;
    private final String identityUrl;

    public AuthenticationFilter(@Value("${identity.service.url}") String identityUrl, WebClient.Builder builder) {
        this.identityUrl = identityUrl;
        this.webClient = builder.baseUrl(identityUrl).build();
    }

    @Override
    public int getOrder() {
        return -1; // ensures this runs before route filters
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Bypass authentication for certain endpoints
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Unauthorized request to {} - Missing or malformed Authorization header", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Validate token via Identity Service
        return webClient.post()
                .uri("/identity/auth/authorize")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .header("userId", request.getHeaders().getFirst("userId"))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return chain.filter(exchange);
                    }
                    log.warn("Unauthorized for {}", path);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                })
                .onErrorResume(ex -> {
                    log.error("Auth service error for {}: {}", path, ex.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });

    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/auth/login")
                || path.startsWith("/auth/refresh")
                || path.startsWith("/identity/auth/authenticate")
                || path.startsWith("/identity/auth/authorize")
                || path.startsWith("/public");
    }

}
