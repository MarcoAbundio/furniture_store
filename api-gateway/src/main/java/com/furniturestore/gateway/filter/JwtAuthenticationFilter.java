package com.furniturestore.gateway.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.crypto.SecretKey;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final SecretKey secretKey;

    // Paths that do not require JWT
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/actuator/health",
            "/eureka"
    );

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Bypass JWT check for public paths and Swagger UI resources
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith)
                || path.contains("/swagger-ui/")
                || path.endsWith("/v3/api-docs");
        if (isPublic) return chain.filter(exchange);

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();

            // Forward user info to downstream services via headers
            ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r.header("X-User-Username", claims.getSubject())
                            .header("X-User-Role", claims.get("role", String.class))
                            .header("X-User-Id", String.valueOf(claims.get("userId"))))
                    .build();

            return chain.filter(mutated);
        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() { return -1; }
}
