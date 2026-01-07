package com.gymmybro.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing an exercise from the ExerciseDB API response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExerciseDbApiResponse {

    /**
     * ExerciseDB unique ID (string, e.g., "0001")
     */
    private String exerciseId;

    /**
     * Exercise name
     */
    private String name;

    /**
     * Target muscles (e.g., ["abs", "biceps"])
     */
    private List<String> targetMuscles;

    /**
     * Body parts (e.g., ["waist", "upper arms"])
     */
    private List<String> bodyParts;

    /**
     * Equipment needed (e.g., ["body weight", "dumbbell"])
     */
    private List<String> equipments;

    /**
     * Image URL showing the exercise
     */
    private String imageUrl;

    /**
     * Secondary muscles worked
     */
    private List<String> secondaryMuscles;

    /**
     * Step-by-step instructions
     */
    private List<String> instructions;
}
