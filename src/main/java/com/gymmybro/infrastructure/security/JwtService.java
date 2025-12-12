package com.gymmybro.infrastructure.security;

import com.gymmybro.config.JwtConfig;
import com.gymmybro.domain.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Service for JWT token generation and validation.
 * Handles both access tokens and refresh tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtConfig jwtConfig;

    /**
     * Generate an access token for a user
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId().toString());

        return generateToken(claims, user.getEmail(),
                jwtConfig.getAccessExpirationMinutes(), getAccessSigningKey());
    }

    /**
     * Generate a refresh token for a user
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("type", "refresh");

        return generateToken(claims, user.getEmail(),
                jwtConfig.getRefreshExpirationMinutes(), getRefreshSigningKey());
    }

    /**
     * Generate a token with specific claims
     */
    private String generateToken(Map<String, Object> extraClaims, String subject,
            int expirationMinutes, SecretKey key) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationMinutes * 60L);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .id(UUID.randomUUID().toString()) // jti for token revocation
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extract username (email) from access token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, getAccessSigningKey());
    }

    /**
     * Extract username from refresh token
     */
    public String extractUsernameFromRefreshToken(String token) {
        return extractClaim(token, Claims::getSubject, getRefreshSigningKey());
    }

    /**
     * Extract JWT ID (jti) from token
     */
    public String extractJti(String token) {
        return extractClaim(token, Claims::getId, getAccessSigningKey());
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration, getAccessSigningKey());
    }

    /**
     * Extract user role from token
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token, getAccessSigningKey());
        return claims.get("role", String.class);
    }

    /**
     * Extract user ID from token
     */
    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token, getAccessSigningKey());
        String userId = claims.get("userId", String.class);
        return userId != null ? UUID.fromString(userId) : null;
    }

    /**
     * Extract a specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, SecretKey key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if access token is valid for the given user
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if refresh token is valid
     */
    public boolean isRefreshTokenValid(String token) {
        try {
            extractAllClaims(token, getRefreshSigningKey());
            return !isRefreshTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Check if refresh token is expired
     */
    private boolean isRefreshTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration, getRefreshSigningKey());
        return expiration.before(new Date());
    }

    /**
     * Get signing key for access tokens
     */
    private SecretKey getAccessSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Get signing key for refresh tokens
     */
    private SecretKey getRefreshSigningKey() {
        byte[] keyBytes = jwtConfig.getRefreshSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Get expiration instant for access token
     */
    public Instant getAccessTokenExpiration() {
        return Instant.now().plusSeconds(jwtConfig.getAccessExpirationMinutes() * 60L);
    }

    /**
     * Get expiration instant for refresh token
     */
    public Instant getRefreshTokenExpiration() {
        return Instant.now().plusSeconds(jwtConfig.getRefreshExpirationMinutes() * 60L);
    }
}
