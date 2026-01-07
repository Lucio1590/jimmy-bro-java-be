package com.gymmybro.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for logging an exercise entry within a workout.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogExerciseEntryRequest {

    @NotNull(message = "Workout exercise ID is required")
    private UUID workoutExerciseId;

    /**
     * Set number (1, 2, 3, etc.)
     */
    @NotNull(message = "Set number is required")
    @Min(value = 1, message = "Set number must be at least 1")
    private Integer setNumber;

    /**
     * Weight used in kg (optional for bodyweight exercises)
     */
    private Double weight;

    /**
     * Actual reps performed
     */
    @Min(value = 0, message = "Reps must be at least 0")
    private Integer repsPerformed;

    /**
     * Duration in seconds (for timed exercises)
     */
    private Integer durationSeconds;

    /**
     * Rate of Perceived Exertion (1-10)
     */
    @Min(value = 1, message = "RPE must be between 1 and 10")
    @Max(value = 10, message = "RPE must be between 1 and 10")
    private Integer rpe;

    /**
     * Optional notes for this set
     */
    private String notes;
}
