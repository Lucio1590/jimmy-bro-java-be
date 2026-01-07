package com.gymmybro.application.dto.response;

import com.gymmybro.domain.workout.WorkoutPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Simplified workout plan response for list views (without nested details).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanSummaryResponse {

    private UUID id;
    private String name;
    private String description;
    private String difficultyLevel;
    private Integer durationWeeks;
    private boolean isActive;
    private Instant createdAt;

    // Creator info
    private UUID creatorId;
    private String creatorName;

    // Counts
    private int dayCount;
    private Long activeAssignmentCount;

    /**
     * Create summary response from entity.
     */
    public static WorkoutPlanSummaryResponse fromEntity(WorkoutPlan plan) {
        WorkoutPlanSummaryResponseBuilder builder = WorkoutPlanSummaryResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .difficultyLevel(plan.getDifficultyLevel())
                .durationWeeks(plan.getDurationWeeks())
                .isActive(plan.isActive())
                .createdAt(plan.getCreatedAt());

        if (plan.getCreatedBy() != null) {
            builder.creatorId(plan.getCreatedBy().getId())
                    .creatorName(plan.getCreatedBy().getFullName());
        }

        if (plan.getWorkoutDays() != null) {
            builder.dayCount(plan.getWorkoutDays().size());
        }

        return builder.build();
    }
}
