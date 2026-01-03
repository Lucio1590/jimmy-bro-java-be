package com.gymmybro.application.service;

import com.gymmybro.application.dto.request.CreateExerciseRequest;
import com.gymmybro.application.dto.response.ExerciseDbApiResponse;
import com.gymmybro.application.dto.response.ExerciseResponse;
import com.gymmybro.application.dto.response.PaginatedResponse;
import com.gymmybro.domain.exercise.Exercise;
import com.gymmybro.domain.exercise.ExerciseRepository;
import com.gymmybro.exception.ResourceNotFoundException;
import com.gymmybro.infrastructure.external.ExerciseDbClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for exercise catalog operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseDbClient exerciseDbClient;

    /**
     * Search exercises with filters.
     */
    public PaginatedResponse<ExerciseResponse> searchExercises(
            String name,
            String bodyPart,
            String targetMuscle,
            String equipment,
            String category,
            Pageable pageable) {

        Page<Exercise> exercisePage = exerciseRepository.findByFilters(
                name, bodyPart, targetMuscle, equipment, category, pageable);

        List<ExerciseResponse> content = exercisePage.getContent().stream()
                .map(ExerciseResponse::fromEntity)
                .toList();

        return PaginatedResponse.<ExerciseResponse>builder()
                .content(content)
                .page(exercisePage.getNumber())
                .size(exercisePage.getSize())
                .totalElements(exercisePage.getTotalElements())
                .totalPages(exercisePage.getTotalPages())
                .first(exercisePage.isFirst())
                .last(exercisePage.isLast())
                .build();
    }

    /**
     * Get exercise by ID.
     */
    public ExerciseResponse getExerciseById(Integer id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));
        return ExerciseResponse.fromEntity(exercise);
    }

    /**
     * Create a custom exercise.
     */
    @Transactional
    public ExerciseResponse createExercise(CreateExerciseRequest request) {
        Exercise exercise = Exercise.builder()
                .name(request.getName())
                .targetMuscle(request.getTargetMuscle())
                .bodyPart(request.getBodyPart())
                .category(request.getCategory())
                .equipment(request.getEquipment())
                .gifUrl(request.getGifUrl())
                .build();

        // Store instructions and secondary muscles in extraData
        if (request.getInstructions() != null || request.getSecondaryMuscles() != null) {
            Map<String, Object> extraData = new HashMap<>();
            if (request.getInstructions() != null) {
                extraData.put("instructions", request.getInstructions());
            }
            if (request.getSecondaryMuscles() != null) {
                extraData.put("secondaryMuscles", request.getSecondaryMuscles());
            }
            exercise.setExtraData(extraData);
        }

        Exercise saved = exerciseRepository.save(exercise);
        log.info("Created custom exercise: {} (ID: {})", saved.getName(), saved.getId());
        return ExerciseResponse.fromEntity(saved);
    }

    /**
     * Trigger ingestion of exercises from ExerciseDB API.
     * Returns number of exercises imported.
     */
    @Transactional
    public int triggerIngestion(int limit) {
        log.info("Starting exercise ingestion from ExerciseDB (limit: {})", limit);

        List<ExerciseDbApiResponse> apiExercises = exerciseDbClient.fetchAllExercises(limit, 0);
        int imported = 0;
        int skipped = 0;

        for (ExerciseDbApiResponse apiExercise : apiExercises) {
            // Skip if already exists
            if (exerciseRepository.existsByExternalId(apiExercise.getId())) {
                skipped++;
                continue;
            }

            Exercise exercise = mapApiResponseToEntity(apiExercise);
            exerciseRepository.save(exercise);
            imported++;
        }

        log.info("Exercise ingestion complete: {} imported, {} skipped (already exist)",
                imported, skipped);
        return imported;
    }

    /**
     * Get all body parts from database.
     */
    public List<String> getAllBodyParts() {
        return exerciseRepository.findAllBodyParts();
    }

    /**
     * Get all target muscles from database.
     */
    public List<String> getAllTargetMuscles() {
        return exerciseRepository.findAllTargetMuscles();
    }

    /**
     * Get all equipment types from database.
     */
    public List<String> getAllEquipment() {
        return exerciseRepository.findAllEquipment();
    }

    /**
     * Get all categories from database.
     */
    public List<String> getAllCategories() {
        return exerciseRepository.findAllCategories();
    }

    /**
     * Map ExerciseDB API response to Exercise entity.
     */
    private Exercise mapApiResponseToEntity(ExerciseDbApiResponse apiResponse) {
        Map<String, Object> extraData = new HashMap<>();
        if (apiResponse.getInstructions() != null) {
            extraData.put("instructions", apiResponse.getInstructions());
        }
        if (apiResponse.getSecondaryMuscles() != null) {
            extraData.put("secondaryMuscles", apiResponse.getSecondaryMuscles());
        }

        return Exercise.builder()
                .externalId(apiResponse.getId())
                .name(apiResponse.getName())
                .targetMuscle(apiResponse.getTarget())
                .bodyPart(apiResponse.getBodyPart())
                .category("STRENGTH") // Default category for imported exercises
                .equipment(apiResponse.getEquipment())
                .gifUrl(apiResponse.getGifUrl())
                .extraData(extraData.isEmpty() ? null : extraData)
                .build();
    }
}
