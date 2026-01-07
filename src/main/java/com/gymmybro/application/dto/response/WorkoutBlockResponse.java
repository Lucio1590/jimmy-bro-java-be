package com.gymmybro.application.dto.response;

import com.gymmybro.domain.workout.WorkoutBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Workout block response with nested exercises.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutBlockResponse {

    private UUID id;
    private Integer blockOrder;
    private String blockType;
    private String name;
    private Integer blockSets;
    private Integer restBetweenRounds;
    private List<WorkoutExerciseResponse> exercises;

    /**
     * Create response from entity.
     */
    public static WorkoutBlockResponse fromEntity(WorkoutBlock block) {
        WorkoutBlockResponseBuilder builder = WorkoutBlockResponse.builder()
                .id(block.getId())
                .blockOrder(block.getBlockOrder())
                .blockType(block.getBlockType())
                .name(block.getName())
                .blockSets(block.getBlockSets())
                .restBetweenRounds(block.getRestBetweenRounds());

        if (block.getWorkoutExercises() != null) {
            builder.exercises(block.getWorkoutExercises().stream()
                    .map(WorkoutExerciseResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }
}
