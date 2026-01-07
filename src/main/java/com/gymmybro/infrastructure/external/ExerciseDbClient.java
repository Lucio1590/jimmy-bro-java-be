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
            ExerciseDbListResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/exercises")
                            .queryParam("limit", limit)
                            .queryParam("offset", offset)
                            .build())
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(ExerciseDbListResponse.class)
                    .block();

            return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
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

        try {
            ExerciseDbListResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/exercises/name/" + name)
                            .build())
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(ExerciseDbListResponse.class)
                    .block();

            return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB search error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Filter exercises by body part.
     */
    @Override
    public List<ExerciseDbApiResponse> filterByBodyPart(String bodyPart) {
        log.info("Filtering exercises by body part: {}", bodyPart);
        return fetchListFromEndpoint("/api/v1/exercises/bodyPart/" + bodyPart);
    }

    /**
     * Filter exercises by target muscle.
     */
    @Override
    public List<ExerciseDbApiResponse> filterByTarget(String target) {
        log.info("Filtering exercises by target: {}", target);
        return fetchListFromEndpoint("/api/v1/exercises/target/" + target);
    }

    /**
     * Filter exercises by equipment type.
     */
    @Override
    public List<ExerciseDbApiResponse> filterByEquipment(String equipment) {
        log.info("Filtering exercises by equipment: {}", equipment);
        return fetchListFromEndpoint("/api/v1/exercises/equipment/" + equipment);
    }

    /**
     * Get list of all body parts.
     */
    @Override
    public List<String> getBodyParts() {
        return fetchListStrings("/api/v1/bodyparts");
    }

    /**
     * Get list of all target muscles.
     */
    @Override
    public List<String> getTargets() {
        return fetchListStrings("/api/v1/muscles");
    }

    /**
     * Get list of all equipment types.
     */
    @Override
    public List<String> getEquipment() {
        return fetchListStrings("/api/v1/equipments");
    }

    private List<ExerciseDbApiResponse> fetchListFromEndpoint(String path) {
        try {
            ExerciseDbListResponse response = webClient.get()
                    .uri(path)
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(ExerciseDbListResponse.class)
                    .block();

            return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("ExerciseDB API error for {}: {} - {}", path, e.getStatusCode(), e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<String> fetchListStrings(String path) {
        try {
            ExerciseDbGenericListResponse response = webClient.get()
                    .uri(path)
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(ExerciseDbGenericListResponse.class)
                    .block();

            return response != null && response.getData() != null
                    ? response.getData().stream().map(ExerciseDbMetadataResponse::getName).toList()
                    : Collections.emptyList();
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
            ExerciseDbSingleResponse response = webClient.get()
                    .uri("/api/v1/exercises/exercise/{id}", externalId)
                    .headers(this::addRapidApiHeaders)
                    .retrieve()
                    .bodyToMono(ExerciseDbSingleResponse.class)
                    .block();

            return response != null ? response.getData() : null;
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
