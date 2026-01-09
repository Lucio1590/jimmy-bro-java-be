package com.gymmybro.infrastructure.external;

import com.gymmybro.application.dto.response.ExerciseDbApiResponse;
import com.gymmybro.config.ExerciseDbConfig;
import com.gymmybro.application.service.ExerciseProvider;
import lombok.extern.slf4j.Slf4j;
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
public class ExerciseDbClient implements ExerciseProvider {

    private final WebClient webClient;
    private final ExerciseDbConfig config;

    public ExerciseDbClient(ExerciseDbConfig config, WebClient.Builder webClientBuilder) {
        this.config = config;
        this.webClient = webClientBuilder
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
    @Override
    public List<ExerciseDbApiResponse> fetchAllExercises(int limit, int offset) {
        log.info("Fetching exercises from ExerciseDB: limit={}, offset={}", limit, offset);

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/exercises")
                            .queryParam("limit", limit)
                            .queryParam("offset", offset)
                            .build())
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<ExerciseDbApiResponse>>() {
                    })
                    .block();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB API error: {} - {}", e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch exercises from ExerciseDB: " + e.getMessage(), e);
        }
    }

    /**
     * Search exercises by name.
     */
    @Override
    public List<ExerciseDbApiResponse> searchByName(String name) {
        log.info("Searching exercises by name: {}", name);
        return fetchListFromEndpoint("/exercises/name/" + name);
    }

    /**
     * Filter exercises by body part.
     */
    @Override
    public List<ExerciseDbApiResponse> filterByBodyPart(String bodyPart) {
        log.info("Filtering exercises by body part: {}", bodyPart);
        return fetchListFromEndpoint("/exercises/bodyPart/" + bodyPart);
    }

    /**
     * Filter exercises by target muscle.
     */
    @Override
    public List<ExerciseDbApiResponse> filterByTarget(String target) {
        log.info("Filtering exercises by target: {}", target);
        return fetchListFromEndpoint("/exercises/target/" + target);
    }

    /**
     * Filter exercises by equipment type.
     */
    @Override
    public List<ExerciseDbApiResponse> filterByEquipment(String equipment) {
        log.info("Filtering exercises by equipment: {}", equipment);
        return fetchListFromEndpoint("/exercises/equipment/" + equipment);
    }

    /**
     * Get list of all body parts.
     */
    @Override
    public List<String> getBodyParts() {
        return fetchListStrings("/exercises/bodyPartList");
    }

    /**
     * Get list of all target muscles.
     */
    @Override
    public List<String> getTargets() {
        return fetchListStrings("/exercises/targetList");
    }

    /**
     * Get list of all equipment types.
     */
    @Override
    public List<String> getEquipment() {
        return fetchListStrings("/exercises/equipmentList");
    }

    private List<ExerciseDbApiResponse> fetchListFromEndpoint(String path) {
        try {
            List<ExerciseDbApiResponse> response = webClient.get()
                    .uri(path)
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<ExerciseDbApiResponse>>() {
                    })
                    .block();

            return response != null ? response : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB API error for {}: {} - {}", path, e.getStatusCode(), e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<String> fetchListStrings(String path) {
        try {
            List<String> response = webClient.get()
                    .uri(path)
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<String>>() {
                    })
                    .block();

            return response != null ? response : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB API error for {}: {} - {}", path, e.getStatusCode(), e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get a single exercise by its ExerciseDB ID.
     *
     * @param externalId The ExerciseDB ID (e.g., "0001")
     * @return Exercise details or null if not found
     */
    @Override
    public ExerciseDbApiResponse getExerciseById(String externalId) {
        log.info("Fetching exercise by ID: {}", externalId);

        try {
            return webClient.get()
                    .uri("/exercises/exercise/{id}", externalId)
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(ExerciseDbApiResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB error fetching exercise {}: {}", externalId, e.getMessage());
            throw new RuntimeException("Failed to fetch exercise from ExerciseDB: " + e.getMessage(), e);
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
