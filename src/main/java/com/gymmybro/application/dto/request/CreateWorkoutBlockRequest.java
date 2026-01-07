package com.gymmybro.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a workout block within a day.
 * A block groups exercises together (e.g., supersets, circuits).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkoutBlockRequest {

    @NotNull(message = "Block order is required")
    @Min(value = 1, message = "Block order must be at least 1")
    private Integer blockOrder;

    /**
     * Block type: STANDARD, SUPERSET, CIRCUIT
     */
    @Builder.Default
    private String blockType = "STANDARD";

    /**
     * Optional notes for the block
     */
    private String notes;

    /**
     * Exercises in this block
     */
    @NotNull(message = "At least one exercise is required in a block")
    @Valid
    private List<CreateWorkoutExerciseRequest> exercises;
}
