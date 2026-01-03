package com.gymmybro.presentation.controller;

import com.gymmybro.application.dto.request.CreateExerciseRequest;
import com.gymmybro.application.dto.response.ExerciseResponse;
import com.gymmybro.application.dto.response.PaginatedResponse;
import com.gymmybro.application.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for exercise catalog operations.
 */
@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercises", description = "Exercise catalog endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ExerciseController {

    private final ExerciseService exerciseService;

    /**
     * Search and list exercises with optional filters.
     */
    @GetMapping
    @Operation(summary = "Search exercises", description = "Search and filter exercises from the catalog with pagination.", responses = {
            @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully")
    })
    public ResponseEntity<PaginatedResponse<ExerciseResponse>> searchExercises(
            @Parameter(description = "Search by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by body part") @RequestParam(required = false) String bodyPart,
            @Parameter(description = "Filter by target muscle") @RequestParam(required = false) String targetMuscle,
            @Parameter(description = "Filter by equipment") @RequestParam(required = false) String equipment,
            @Parameter(description = "Filter by category") @RequestParam(required = false) String category,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(exerciseService.searchExercises(
                name, bodyPart, targetMuscle, equipment, category, pageable));
    }

    /**
     * Get exercise by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get exercise by ID", description = "Get details of a specific exercise.", responses = {
            @ApiResponse(responseCode = "200", description = "Exercise retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    public ResponseEntity<ExerciseResponse> getExerciseById(
            @Parameter(description = "Exercise ID") @PathVariable Integer id) {
        return ResponseEntity.ok(exerciseService.getExerciseById(id));
    }

    /**
     * Create a custom exercise (Admin only).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create custom exercise", description = "Create a new exercise in the catalog. Admin only.", responses = {
            @ApiResponse(responseCode = "200", description = "Exercise created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin only")
    })
    public ResponseEntity<ExerciseResponse> createExercise(
            @Valid @RequestBody CreateExerciseRequest request) {
        return ResponseEntity.ok(exerciseService.createExercise(request));
    }

    /**
     * Trigger exercise ingestion from ExerciseDB API (Admin only).
     */
    @PostMapping("/ingest/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Trigger ExerciseDB ingestion", description = "Import exercises from ExerciseDB API. Admin only.", responses = {
            @ApiResponse(responseCode = "200", description = "Ingestion completed"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin only")
    })
    public ResponseEntity<Map<String, Object>> triggerIngestion(
            @Parameter(description = "Max exercises to import") @RequestParam(defaultValue = "100") int limit) {
        int imported = exerciseService.triggerIngestion(limit);
        return ResponseEntity.ok(Map.of(
                "message", "Exercise ingestion completed",
                "imported", imported));
    }

    /**
     * Get all available body parts.
     */
    @GetMapping("/metadata/body-parts")
    @Operation(summary = "Get body parts", description = "Get list of all body parts available in the catalog.", responses = {
            @ApiResponse(responseCode = "200", description = "Body parts retrieved")
    })
    public ResponseEntity<List<String>> getBodyParts() {
        return ResponseEntity.ok(exerciseService.getAllBodyParts());
    }

    /**
     * Get all available target muscles.
     */
    @GetMapping("/metadata/targets")
    @Operation(summary = "Get target muscles", description = "Get list of all target muscles available in the catalog.", responses = {
            @ApiResponse(responseCode = "200", description = "Target muscles retrieved")
    })
    public ResponseEntity<List<String>> getTargetMuscles() {
        return ResponseEntity.ok(exerciseService.getAllTargetMuscles());
    }

    /**
     * Get all available equipment types.
     */
    @GetMapping("/metadata/equipment")
    @Operation(summary = "Get equipment types", description = "Get list of all equipment types available in the catalog.", responses = {
            @ApiResponse(responseCode = "200", description = "Equipment types retrieved")
    })
    public ResponseEntity<List<String>> getEquipment() {
        return ResponseEntity.ok(exerciseService.getAllEquipment());
    }

    /**
     * Get all available categories.
     */
    @GetMapping("/metadata/categories")
    @Operation(summary = "Get categories", description = "Get list of all exercise categories available in the catalog.", responses = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved")
    })
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(exerciseService.getAllCategories());
    }
}
