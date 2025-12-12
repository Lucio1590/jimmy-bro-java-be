package com.gymmybro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for JWT authentication.
 * Maps to the 'jwt' section in application.yml
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {

    /**
     * Secret key for signing access tokens (must be at least 256 bits / 32 bytes)
     */
    private String secret;

    /**
     * Secret key for signing refresh tokens
     */
    private String refreshSecret;

    /**
     * Access token expiration time in minutes
     */
    private int accessExpirationMinutes = 60;

    /**
     * Refresh token expiration time in minutes (default: 7 days)
     */
    private int refreshExpirationMinutes = 10080;
}
