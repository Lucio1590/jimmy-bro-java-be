package com.gymmybro.application.dto.response;

import com.gymmybro.domain.workout.WorkoutPlanAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Workout assignment response with plan and trainee details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutAssignmentResponse {

    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private Instant assignedAt;

    // Trainee info
    private UUID traineeId;
    private String traineeName;
    private String traineeEmail;

    // Plan info
    private UUID workoutPlanId;
    private String workoutPlanName;

    // Assigned by (PT) info
    private UUID assignedById;
    private String assignedByName;

    /**
     * Create response from entity.
     */
    public static WorkoutAssignmentResponse fromEntity(WorkoutPlanAssignment assignment) {
        WorkoutAssignmentResponseBuilder builder = WorkoutAssignmentResponse.builder()
                .id(assignment.getId())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .isActive(assignment.isActive())
                .assignedAt(assignment.getAssignedAt());

        if (assignment.getTrainee() != null) {
            builder.traineeId(assignment.getTrainee().getId())
                    .traineeName(assignment.getTrainee().getFullName())
                    .traineeEmail(assignment.getTrainee().getEmail());
        }

        if (assignment.getWorkoutPlan() != null) {
            builder.workoutPlanId(assignment.getWorkoutPlan().getId())
                    .workoutPlanName(assignment.getWorkoutPlan().getName());
        }

        if (assignment.getAssignedBy() != null) {
            builder.assignedById(assignment.getAssignedBy().getId())
                    .assignedByName(assignment.getAssignedBy().getFullName());
        }

        return builder.build();
    }
}
