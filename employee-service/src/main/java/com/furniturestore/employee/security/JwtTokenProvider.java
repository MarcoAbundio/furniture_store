package com.furniturestore.employee.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String getUsernameFromToken(String token) { return parseClaims(token).getSubject(); }
    public String getRoleFromToken(String token) { return parseClaims(token).get("role", String.class); }
    public boolean validateToken(String token) {
        try { parseClaims(token); return true; } catch (JwtException | IllegalArgumentException e) { return false; }
    }
    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}
