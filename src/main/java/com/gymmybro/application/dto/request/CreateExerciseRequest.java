package com.gymmybro.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a custom exercise.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExerciseRequest {

    @NotBlank(message = "Exercise name is required")
    private String name;

    private String targetMuscle;

    private String bodyPart;

    @NotBlank(message = "Category is required")
    private String category;

    private String equipment;

    private String gifUrl;

    private List<String> instructions;

    private List<String> secondaryMuscles;
}
