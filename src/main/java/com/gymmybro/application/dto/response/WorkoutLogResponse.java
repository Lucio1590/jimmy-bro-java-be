package com.gymmybro.application.dto.response;

import com.gymmybro.domain.workout.WorkoutLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Response DTO for a completed workout log.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogResponse {

    private UUID id;
    private LocalDate workoutDate;
    private Instant startedAt;
    private Instant completedAt;
    private Integer durationMinutes;
    private String notes;
    private Integer rating;
    private Instant createdAt;

    // Workout day info
    private UUID workoutDayId;
    private String workoutDayName;
    private Integer dayNumber;

    // Plan info
    private UUID workoutPlanId;
    private String workoutPlanName;

    // Logged entries
    private List<WorkoutLogEntryResponse> entries;

    /**
     * Create response from entity.
     */
    public static WorkoutLogResponse fromEntity(WorkoutLog log) {
        WorkoutLogResponseBuilder builder = WorkoutLogResponse.builder()
                .id(log.getId())
                .workoutDate(log.getWorkoutDate())
                .startedAt(log.getStartedAt())
                .completedAt(log.getCompletedAt())
                .durationMinutes(log.getDurationMinutes())
                .notes(log.getNotes())
                .rating(log.getRating())
                .createdAt(log.getCreatedAt());

        if (log.getWorkoutDay() != null) {
            builder.workoutDayId(log.getWorkoutDay().getId())
                    .workoutDayName(log.getWorkoutDay().getName())
                    .dayNumber(log.getWorkoutDay().getDayNumber());

            if (log.getWorkoutDay().getWorkoutPlan() != null) {
                builder.workoutPlanId(log.getWorkoutDay().getWorkoutPlan().getId())
                        .workoutPlanName(log.getWorkoutDay().getWorkoutPlan().getName());
            }
        }

        if (log.getEntries() != null) {
            builder.entries(log.getEntries().stream()
                    .map(WorkoutLogEntryResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }
}
