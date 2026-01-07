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
                .id(apiResponse.getExerciseId())
                .name(apiResponse.getName())
                .target(getFirstOrNull(apiResponse.getTargetMuscles()))
                .bodyPart(getFirstOrNull(apiResponse.getBodyParts()))
                .equipment(getFirstOrNull(apiResponse.getEquipments()))
                .gifUrl(apiResponse.getImageUrl())
                .instructions(apiResponse.getInstructions())
                .secondaryMuscles(apiResponse.getSecondaryMuscles())
                .build();
    }

    private static String getFirstOrNull(List<String> list) {
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }
}
