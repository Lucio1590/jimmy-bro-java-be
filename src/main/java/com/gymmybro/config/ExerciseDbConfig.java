package com.gymmybro.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for ExerciseDB API (RapidAPI).
 */
@Configuration
@ConfigurationProperties(prefix = "exercisedb")
@Getter
@Setter
public class ExerciseDbConfig {

    /**
     * Base URL for ExerciseDB API.
     * Use RapidAPI host for paid tier, or open source host for free tier.
     */
    private String baseUrl = "https://exercisedb-api1.p.rapidapi.com";

    /**
     * RapidAPI key for authentication (required for RapidAPI host).
     */
    private String apiKey;

    /**
     * RapidAPI host header value.
     */
    private String host = "exercisedb-api1.p.rapidapi.com";

    /**
     * Check if RapidAPI authentication is configured.
     */
    public boolean isRapidApiEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }
}
