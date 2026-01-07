package com.gymmybro.application.service;

import com.gymmybro.application.dto.response.ExerciseDbApiResponse;
import com.gymmybro.application.dto.response.ExerciseResponse;
import com.gymmybro.application.dto.response.PaginatedResponse;
import com.gymmybro.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for exercise catalog operations.
 * Proxies all requests directly to ExerciseDB API.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseService {

    private final ExerciseProvider exerciseProvider;

    /**
     * Search exercises by name.
     *
     * @param name   Search query
     * @param limit  Number of results to return
     * @param offset Starting position
     * @return Paginated exercise responses
     */
    public PaginatedResponse<ExerciseResponse> searchExercises(
            String name,
            int limit,
            int offset) {

        List<ExerciseDbApiResponse> apiResults;

        if (name != null && !name.isBlank()) {
            apiResults = exerciseProvider.searchByName(name);
        } else {
            apiResults = exerciseProvider.fetchAllExercises(limit, offset);
        }

        List<ExerciseResponse> content = apiResults.stream()
                .map(ExerciseResponse::fromApiResponse)
                .toList();

        return PaginatedResponse.<ExerciseResponse>builder()
                .content(content)
                .page(offset / limit)
                .size(limit)
                .totalElements(content.size())
                .totalPages(1)
                .first(offset == 0)
                .last(true)
                .build();
    }

    /**
     * Get all exercises with pagination.
     *
     * @param limit  Number of exercises to fetch (max 1000)
     * @param offset Starting position
     * @return Paginated exercise responses
     */
    public PaginatedResponse<ExerciseResponse> getAllExercises(int limit, int offset) {
        List<ExerciseDbApiResponse> apiResults = exerciseProvider.fetchAllExercises(limit, offset);

        List<ExerciseResponse> content = apiResults.stream()
                .map(ExerciseResponse::fromApiResponse)
                .toList();

        return PaginatedResponse.<ExerciseResponse>builder()
                .content(content)
                .page(offset / limit)
                .size(limit)
                .totalElements(content.size())
                .totalPages(1)
                .first(offset == 0)
                .last(content.size() < limit)
                .build();
    }

    /**
     * Get exercise by ExerciseDB external ID.
     *
     * @param externalId The ExerciseDB ID (e.g., "0001")
     * @return Exercise response
     */
    public ExerciseResponse getExerciseById(String externalId) {
        ExerciseDbApiResponse apiResponse = exerciseProvider.getExerciseById(externalId);

        if (apiResponse == null) {
            throw new ResourceNotFoundException("Exercise", "externalId", externalId);
        }

        return ExerciseResponse.fromApiResponse(apiResponse);
    }

    /**
     * Filter exercises by body part.
     *
     * @param bodyPart Body part to filter by (e.g., "chest", "back")
     * @return List of exercise responses
     */
    public List<ExerciseResponse> filterByBodyPart(String bodyPart) {
        List<ExerciseDbApiResponse> apiResults = exerciseProvider.filterByBodyPart(bodyPart);
        return apiResults.stream()
                .map(ExerciseResponse::fromApiResponse)
                .toList();
    }

    /**
     * Filter exercises by target muscle.
     *
     * @param target Target muscle to filter by (e.g., "biceps", "pectorals")
     * @return List of exercise responses
     */
    public List<ExerciseResponse> filterByTarget(String target) {
        List<ExerciseDbApiResponse> apiResults = exerciseProvider.filterByTarget(target);
        return apiResults.stream()
                .map(ExerciseResponse::fromApiResponse)
                .toList();
    }

    /**
     * Filter exercises by equipment.
     *
     * @param equipment Equipment to filter by (e.g., "barbell", "dumbbell")
     * @return List of exercise responses
     */
    public List<ExerciseResponse> filterByEquipment(String equipment) {
        List<ExerciseDbApiResponse> apiResults = exerciseProvider.filterByEquipment(equipment);
        return apiResults.stream()
                .map(ExerciseResponse::fromApiResponse)
                .toList();
    }

    /**
     * Get all body parts from ExerciseDB.
     *
     * @return List of body parts
     */
    public List<String> getAllBodyParts() {
        return exerciseProvider.getBodyParts();
    }

    /**
     * Get all target muscles from ExerciseDB.
     *
     * @return List of target muscles
     */
    public List<String> getAllTargetMuscles() {
        return exerciseProvider.getTargets();
    }

    /**
     * Get all equipment types from ExerciseDB.
     *
     * @return List of equipment types
     */
    public List<String> getAllEquipment() {
        return exerciseProvider.getEquipment();
    }
}
