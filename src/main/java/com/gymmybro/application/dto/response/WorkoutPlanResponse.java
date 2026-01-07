package com.gymmybro.application.dto.response;

import com.gymmybro.domain.workout.WorkoutPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Full workout plan response with nested days, blocks, and exercises.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanResponse {

    private UUID id;
    private String name;
    private String description;
    private String difficultyLevel;
    private Integer durationWeeks;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

    // Creator info
    private UUID creatorId;
    private String creatorName;
    private String creatorEmail;

    // Nested days with blocks and exercises
    private List<WorkoutDayResponse> days;

    // Assignment count
    private Long activeAssignmentCount;

    /**
     * Create full response with nested details from entity.
     */
    public static WorkoutPlanResponse fromEntity(WorkoutPlan plan) {
        return fromEntity(plan, true);
    }

    /**
     * Create response from entity with optional nested details.
     */
    public static WorkoutPlanResponse fromEntity(WorkoutPlan plan, boolean includeDetails) {
        WorkoutPlanResponseBuilder builder = WorkoutPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .difficultyLevel(plan.getDifficultyLevel())
                .durationWeeks(plan.getDurationWeeks())
                .isActive(plan.isActive())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt());

        // Add creator info if available
        if (plan.getCreatedBy() != null) {
            builder.creatorId(plan.getCreatedBy().getId())
                    .creatorName(plan.getCreatedBy().getFullName())
                    .creatorEmail(plan.getCreatedBy().getEmail());
        }

        // Add nested days if requested
        if (includeDetails && plan.getWorkoutDays() != null) {
            builder.days(plan.getWorkoutDays().stream()
                    .map(WorkoutDayResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }
}
