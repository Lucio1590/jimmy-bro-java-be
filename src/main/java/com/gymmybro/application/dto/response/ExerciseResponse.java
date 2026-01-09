package com.gymmybro.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Exercise response DTO for API responses.
 * Data comes directly from ExerciseDB API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {

    private String id; // ExerciseDB external ID
    private String name;
    private String target;
    private String bodyPart;
    private String equipment;
    private String gifUrl;
    private List<String> instructions;
    private List<String> secondaryMuscles;

    /**
     * Create ExerciseResponse from ExerciseDB API response.
     */
    public static ExerciseResponse fromApiResponse(ExerciseDbApiResponse apiResponse) {
        return ExerciseResponse.builder()
                .id(apiResponse.getId())
                .name(apiResponse.getName())
                .target(apiResponse.getTarget())
                .bodyPart(apiResponse.getBodyPart())
                .equipment(apiResponse.getEquipment())
                .gifUrl(apiResponse.getGifUrl())
                .instructions(apiResponse.getInstructions())
                .secondaryMuscles(apiResponse.getSecondaryMuscles())
                .build();
    }
}
