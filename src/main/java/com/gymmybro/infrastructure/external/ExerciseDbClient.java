package com.gymmybro.infrastructure.external;

import com.gymmybro.application.dto.response.ExerciseDbApiResponse;
import com.gymmybro.config.ExerciseDbConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;

/**
 * HTTP client for ExerciseDB API (RapidAPI).
 * Fetches exercise data for catalog population.
 */
@Component
@Slf4j
public class ExerciseDbClient {

    private final WebClient webClient;
    private final ExerciseDbConfig config;

    public ExerciseDbClient(ExerciseDbConfig config) {
        this.config = config;
        this.webClient = WebClient.builder()
                .baseUrl(config.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Fetch all exercises with pagination.
     * 
     * @param limit  Number of exercises to fetch (max 1000)
     * @param offset Starting position
     * @return List of exercises from API
     */
    public List<ExerciseDbApiResponse> fetchAllExercises(int limit, int offset) {
        log.info("Fetching exercises from ExerciseDB: limit={}, offset={}", limit, offset);

        try {
            List<ExerciseDbApiResponse> exercises = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/exercises")
                            .queryParam("limit", limit)
                            .queryParam("offset", offset)
                            .build())
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ExerciseDbApiResponse>>() {
                    })
                    .block();

            log.info("Fetched {} exercises from ExerciseDB", exercises != null ? exercises.size() : 0);
            return exercises != null ? exercises : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB API error: {} - {}", e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch exercises from ExerciseDB: " + e.getMessage(), e);
        }
    }

    /**
     * Search exercises by name.
     */
    public List<ExerciseDbApiResponse> searchByName(String name) {
        log.info("Searching exercises by name: {}", name);

        try {
            List<ExerciseDbApiResponse> exercises = webClient.get()
                    .uri("/exercises/name/{name}", name.toLowerCase())
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ExerciseDbApiResponse>>() {
                    })
                    .block();

            return exercises != null ? exercises : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB search error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Filter exercises by body part.
     */
    public List<ExerciseDbApiResponse> filterByBodyPart(String bodyPart) {
        log.info("Filtering exercises by body part: {}", bodyPart);

        try {
            List<ExerciseDbApiResponse> exercises = webClient.get()
                    .uri("/exercises/bodyPart/{bodyPart}", bodyPart.toLowerCase())
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ExerciseDbApiResponse>>() {
                    })
                    .block();

            return exercises != null ? exercises : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB filter error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Filter exercises by target muscle.
     */
    public List<ExerciseDbApiResponse> filterByTarget(String target) {
        log.info("Filtering exercises by target: {}", target);

        try {
            List<ExerciseDbApiResponse> exercises = webClient.get()
                    .uri("/exercises/target/{target}", target.toLowerCase())
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ExerciseDbApiResponse>>() {
                    })
                    .block();

            return exercises != null ? exercises : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB filter error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get list of all body parts.
     */
    public List<String> getBodyParts() {
        try {
            List<String> bodyParts = webClient.get()
                    .uri("/exercises/bodyPartList")
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                    })
                    .block();

            return bodyParts != null ? bodyParts : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB error fetching body parts: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get list of all target muscles.
     */
    public List<String> getTargets() {
        try {
            List<String> targets = webClient.get()
                    .uri("/exercises/targetList")
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                    })
                    .block();

            return targets != null ? targets : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB error fetching targets: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get list of all equipment types.
     */
    public List<String> getEquipment() {
        try {
            List<String> equipment = webClient.get()
                    .uri("/exercises/equipmentList")
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                    })
                    .block();

            return equipment != null ? equipment : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB error fetching equipment: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Add RapidAPI authentication headers.
     */
    private void addRapidApiHeaders(HttpHeaders headers) {
        headers.set("X-RapidAPI-Key", config.getApiKey());
        headers.set("X-RapidAPI-Host", config.getHost());
    }
}
