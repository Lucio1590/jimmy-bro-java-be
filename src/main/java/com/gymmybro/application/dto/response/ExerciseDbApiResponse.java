package com.gymmybro.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String id;

    /**
     * Exercise name
     */
    private String name;

    /**
     * Target muscle (e.g., "abs", "biceps")
     */
    private String target;

    /**
     * Body part (e.g., "waist", "upper arms")
     */
    private String bodyPart;

    /**
     * Equipment needed (e.g., "body weight", "dumbbell")
     */
    private String equipment;

    /**
     * GIF URL showing the exercise
     */
    private String gifUrl;

    /**
     * Secondary muscles worked
     */
    private List<String> secondaryMuscles;

    /**
     * Step-by-step instructions
     */
    private List<String> instructions;
}
