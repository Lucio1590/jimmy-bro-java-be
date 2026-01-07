package com.gymmybro.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding an exercise to a workout block.
 * Uses ExerciseDB external ID instead of local database ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkoutExerciseRequest {

    @NotBlank(message = "Exercise external ID is required")
    private String exerciseExternalId;

    @NotNull(message = "Order is required")
    @Min(value = 1, message = "Order must be at least 1")
    private Integer exerciseOrder;

    @NotNull(message = "Number of sets is required")
    @Min(value = 1, message = "Sets must be at least 1")
    private Integer sets;

    /**
     * Target reps per set (can be null for time-based exercises)
     */
    private Integer reps;

    /**
     * Duration in seconds (for time-based exercises like planks)
     */
    private Integer durationSeconds;

    /**
     * Rest time in seconds between sets
     */
    @Builder.Default
    private Integer restSeconds = 60;

    /**
     * Optional notes (e.g., "slow negatives", "pause at bottom")
     */
    private String notes;
}
