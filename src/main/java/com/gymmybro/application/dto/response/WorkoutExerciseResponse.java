package com.gymmybro.application.dto.response;

import com.gymmybro.domain.workout.WorkoutExercise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Workout exercise response with exercise details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseResponse {

    private UUID id;
    private Integer exerciseOrder;
    private Integer sets;
    private String targetReps;
    private Double targetWeight;
    private Integer restSeconds;
    private Integer durationSeconds;
    private String tempo;
    private String notes;

    // Exercise details (from ExerciseDB or cached)
    private String exerciseExternalId;
    private String exerciseName;
    private String exerciseGifUrl;
    private String targetMuscle;
    private String equipment;

    /**
     * Create response from entity.
     * Uses cached exercise name and GIF URL stored in the entity.
     */
    public static WorkoutExerciseResponse fromEntity(WorkoutExercise workoutExercise) {
        return WorkoutExerciseResponse.builder()
                .id(workoutExercise.getId())
                .exerciseOrder(workoutExercise.getExerciseOrder())
                .sets(workoutExercise.getSets())
                .targetReps(workoutExercise.getTargetReps())
                .targetWeight(workoutExercise.getTargetWeight())
                .restSeconds(workoutExercise.getRestSeconds())
                .durationSeconds(workoutExercise.getDurationSeconds())
                .tempo(workoutExercise.getTempo())
                .notes(workoutExercise.getNotes())
                .exerciseExternalId(workoutExercise.getExerciseExternalId())
                .exerciseName(workoutExercise.getExerciseName())
                .exerciseGifUrl(workoutExercise.getExerciseGifUrl())
                .build();
    }

    /**
     * Create response from entity with additional exercise details from API.
     */
    public static WorkoutExerciseResponse fromEntityWithApiDetails(
            WorkoutExercise workoutExercise,
            ExerciseDbApiResponse apiResponse) {
        WorkoutExerciseResponseBuilder builder = WorkoutExerciseResponse.builder()
                .id(workoutExercise.getId())
                .exerciseOrder(workoutExercise.getExerciseOrder())
                .sets(workoutExercise.getSets())
                .targetReps(workoutExercise.getTargetReps())
                .targetWeight(workoutExercise.getTargetWeight())
                .restSeconds(workoutExercise.getRestSeconds())
                .durationSeconds(workoutExercise.getDurationSeconds())
                .tempo(workoutExercise.getTempo())
                .notes(workoutExercise.getNotes())
                .exerciseExternalId(workoutExercise.getExerciseExternalId());

        if (apiResponse != null) {
            builder.exerciseName(apiResponse.getName())
                    .exerciseGifUrl(apiResponse.getGifUrl())
                    .targetMuscle(apiResponse.getTarget())
                    .equipment(apiResponse.getEquipment());
        } else {
            // Fall back to cached values
            builder.exerciseName(workoutExercise.getExerciseName())
                    .exerciseGifUrl(workoutExercise.getExerciseGifUrl());
        }

        return builder.build();
    }
}
