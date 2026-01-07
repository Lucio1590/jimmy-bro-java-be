package com.gymmybro.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for updating an existing workout plan.
 * All fields are optional - only specified fields will be updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWorkoutPlanRequest {

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
     * If provided, replaces all existing days
     */
    @Valid
    private List<CreateWorkoutDayRequest> days;
}
