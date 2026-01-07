package com.gymmybro.application.dto.response;

import com.gymmybro.domain.workout.WorkoutLogEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for a single workout log entry (set).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogEntryResponse {

    private UUID id;
    private Integer setNumber;
    private Integer actualReps;
    private Double actualWeight;
    private Integer actualDurationSeconds;
    private Integer rpe;
    private boolean isCompleted;
    private String notes;

    // Exercise info
    private UUID workoutExerciseId;
    private String exerciseExternalId;
    private String exerciseName;

    /**
     * Create response from entity.
     */
    public static WorkoutLogEntryResponse fromEntity(WorkoutLogEntry entry) {
        WorkoutLogEntryResponseBuilder builder = WorkoutLogEntryResponse.builder()
                .id(entry.getId())
                .setNumber(entry.getSetNumber())
                .actualReps(entry.getActualReps())
                .actualWeight(entry.getActualWeight())
                .actualDurationSeconds(entry.getActualDurationSeconds())
                .rpe(entry.getRpe())
                .isCompleted(entry.isCompleted())
                .notes(entry.getNotes());

        if (entry.getWorkoutExercise() != null) {
            builder.workoutExerciseId(entry.getWorkoutExercise().getId())
                    .exerciseExternalId(entry.getWorkoutExercise().getExerciseExternalId())
                    .exerciseName(entry.getWorkoutExercise().getExerciseName());
        }

        return builder.build();
    }
}
