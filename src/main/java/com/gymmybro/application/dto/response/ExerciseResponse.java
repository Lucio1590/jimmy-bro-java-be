package com.gymmybro.application.dto.response;

import com.gymmybro.domain.exercise.Exercise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Exercise response DTO for API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {

    private Integer id;
    private String externalId;
    private String name;
    private String targetMuscle;
    private String bodyPart;
    private String category;
    private String equipment;
    private String gifUrl;
    private List<String> instructions;
    private List<String> secondaryMuscles;

    /**
     * Create ExerciseResponse from entity.
     */
    @SuppressWarnings("unchecked")
    public static ExerciseResponse fromEntity(Exercise exercise) {
        ExerciseResponse.ExerciseResponseBuilder builder = ExerciseResponse.builder()
                .id(exercise.getId())
                .externalId(exercise.getExternalId())
                .name(exercise.getName())
                .targetMuscle(exercise.getTargetMuscle())
                .bodyPart(exercise.getBodyPart())
                .category(exercise.getCategory())
                .equipment(exercise.getEquipment())
                .gifUrl(exercise.getGifUrl());

        // Extract extra data if present
        Map<String, Object> extraData = exercise.getExtraData();
        if (extraData != null) {
            if (extraData.containsKey("instructions")) {
                builder.instructions((List<String>) extraData.get("instructions"));
            }
            if (extraData.containsKey("secondaryMuscles")) {
                builder.secondaryMuscles((List<String>) extraData.get("secondaryMuscles"));
            }
        }

        return builder.build();
    }
}
