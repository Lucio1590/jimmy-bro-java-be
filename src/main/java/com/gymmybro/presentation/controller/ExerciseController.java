package com.gymmybro.presentation.controller;

import com.gymmybro.application.dto.response.ExerciseResponse;
import com.gymmybro.application.dto.response.PaginatedResponse;
import com.gymmybro.application.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for exercise catalog operations.
 * Proxies all requests to ExerciseDB RapidAPI.
 */
@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercises", description = "Exercise catalog endpoints - powered by ExerciseDB")
@SecurityRequirement(name = "bearerAuth")
public class ExerciseController {

        private final ExerciseService exerciseService;

        /**
         * Search and list exercises from ExerciseDB.
         */
        @GetMapping
        @Operation(summary = "List exercises", description = "Get exercises from ExerciseDB with optional name search.", responses = {
                        @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully")
        })
        public ResponseEntity<PaginatedResponse<ExerciseResponse>> getExercises(
                        @Parameter(description = "Search by name") @RequestParam(required = false) String name,
                        @Parameter(description = "Number of results (max 1000)") @RequestParam(defaultValue = "20") int limit,
                        @Parameter(description = "Starting position") @RequestParam(defaultValue = "0") int offset) {

                return ResponseEntity.ok(exerciseService.searchExercises(name, limit, offset));
        }

        /**
         * Get exercise by ExerciseDB ID.
         */
        @GetMapping("/{externalId}")
        @Operation(summary = "Get exercise by ID", description = "Get details of a specific exercise from ExerciseDB.", responses = {
                        @ApiResponse(responseCode = "200", description = "Exercise retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Exercise not found")
        })
        public ResponseEntity<ExerciseResponse> getExerciseById(
                        @Parameter(description = "ExerciseDB ID (e.g., '0001')") @PathVariable String externalId) {
                return ResponseEntity.ok(exerciseService.getExerciseById(externalId));
        }

        /**
         * Filter exercises by body part.
         */
        @GetMapping("/bodyPart/{bodyPart}")
        @Operation(summary = "Filter by body part", description = "Get exercises targeting a specific body part.", responses = {
                        @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully")
        })
        public ResponseEntity<List<ExerciseResponse>> filterByBodyPart(
                        @Parameter(description = "Body part (e.g., 'chest', 'back')") @PathVariable String bodyPart) {
                return ResponseEntity.ok(exerciseService.filterByBodyPart(bodyPart));
        }

        /**
         * Filter exercises by target muscle.
         */
        @GetMapping("/target/{target}")
        @Operation(summary = "Filter by target muscle", description = "Get exercises targeting a specific muscle.", responses = {
                        @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully")
        })
        public ResponseEntity<List<ExerciseResponse>> filterByTarget(
                        @Parameter(description = "Target muscle (e.g., 'biceps', 'pectorals')") @PathVariable String target) {
                return ResponseEntity.ok(exerciseService.filterByTarget(target));
        }

        /**
         * Filter exercises by equipment.
         */
        @GetMapping("/equipment/{equipment}")
        @Operation(summary = "Filter by equipment", description = "Get exercises using specific equipment.", responses = {
                        @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully")
        })
        public ResponseEntity<List<ExerciseResponse>> filterByEquipment(
                        @Parameter(description = "Equipment (e.g., 'barbell', 'dumbbell')") @PathVariable String equipment) {
                return ResponseEntity.ok(exerciseService.filterByEquipment(equipment));
        }

        /**
         * Get all available body parts.
         */
        @GetMapping("/metadata/body-parts")
        @Operation(summary = "Get body parts", description = "Get list of all body parts available in ExerciseDB.", responses = {
                        @ApiResponse(responseCode = "200", description = "Body parts retrieved")
        })
        public ResponseEntity<List<String>> getBodyParts() {
                return ResponseEntity.ok(exerciseService.getAllBodyParts());
        }

        /**
         * Get all available categories (alias for body parts).
         */
        @GetMapping("/metadata/categories")
        @Operation(summary = "Get categories", description = "Get list of all exercise categories (mapped to body parts).", responses = {
                        @ApiResponse(responseCode = "200", description = "Categories retrieved")
        })
        public ResponseEntity<List<String>> getCategories(
                        @Parameter(description = "Search query") @RequestParam(required = false, name = "q") String query) {
                List<String> categories = exerciseService.getAllBodyParts();
                if (query != null && !query.isBlank()) {
                        String lowerQuery = query.toLowerCase();
                        categories = categories.stream()
                                        .filter(c -> c.toLowerCase().contains(lowerQuery))
                                        .toList();
                }
                return ResponseEntity.ok(categories);
        }

        /**
         * Get all available target muscles.
         */
        @GetMapping("/metadata/targets")
        @Operation(summary = "Get target muscles", description = "Get list of all target muscles available in ExerciseDB.", responses = {
                        @ApiResponse(responseCode = "200", description = "Target muscles retrieved")
        })
        public ResponseEntity<List<String>> getTargetMuscles() {
                return ResponseEntity.ok(exerciseService.getAllTargetMuscles());
        }

        /**
         * Get all available equipment types.
         */
        @GetMapping("/metadata/equipment")
        @Operation(summary = "Get equipment types", description = "Get list of all equipment types available in ExerciseDB.", responses = {
                        @ApiResponse(responseCode = "200", description = "Equipment types retrieved")
        })
        public ResponseEntity<List<String>> getEquipment() {
                return ResponseEntity.ok(exerciseService.getAllEquipment());
        }
}
