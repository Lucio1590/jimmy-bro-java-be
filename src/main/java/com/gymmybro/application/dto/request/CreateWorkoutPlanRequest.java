package com.gymmybro.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a workout plan with nested days, blocks, and
 * exercises.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkoutPlanRequest {

    @NotBlank(message = "Plan name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    private String description;

    /**
     * Difficulty level: BEGINNER, INTERMEDIATE, ADVANCED
     */
    private String difficultyLevel;

    /**
     * Duration of the program in weeks
     */
    private Integer durationWeeks;

    /**
     * Nested workout days with blocks and exercises
     */
    @NotNull(message = "At least one workout day is required")
    @Size(min = 1, message = "At least one workout day is required")
    @Valid
    private List<CreateWorkoutDayRequest> days;
}
