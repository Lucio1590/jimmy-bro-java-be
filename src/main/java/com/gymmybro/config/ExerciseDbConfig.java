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
     * Base URL for ExerciseDB API
     */
    private String baseUrl = "https://exercisedb.p.rapidapi.com";

    /**
     * RapidAPI key for authentication
     */
    private String apiKey;

    /**
     * RapidAPI host header value
     */
    private String host = "exercisedb.p.rapidapi.com";
}
