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

    @JsonProperty("exerciseId")
    private String id;

    private String name;

    @JsonProperty("targetMuscles")
    private List<String> targetMuscles;

    @JsonProperty("bodyParts")
    private List<String> bodyParts;

    @JsonProperty("equipments")
    private List<String> equipments;

    private String gifUrl;

    private List<String> secondaryMuscles;

    private List<String> instructions;

    // Compatibility getters
    public String getTarget() {
        return (targetMuscles != null && !targetMuscles.isEmpty()) ? targetMuscles.get(0) : null;
    }

    public String getBodyPart() {
        return (bodyParts != null && !bodyParts.isEmpty()) ? bodyParts.get(0) : null;
    }

    public String getEquipment() {
        return (equipments != null && !equipments.isEmpty()) ? equipments.get(0) : null;
    }
}
