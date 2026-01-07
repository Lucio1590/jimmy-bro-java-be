package com.gymmybro.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for today's active workout.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveWorkoutResponse {

    /**
     * Whether the trainee has an active assignment
     */
    private boolean hasActiveAssignment;

    /**
     * Today's workout day (if applicable)
     */
    private UUID workoutDayId;
    private String workoutDayName;
    private Integer dayNumber;
    private boolean isRestDay;

    /**
     * The workout plan info
     */
    private UUID workoutPlanId;
    private String workoutPlanName;

    /**
     * Current workout log ID (if workout already started today)
     */
    private UUID currentLogId;
    private boolean workoutInProgress;
    private boolean workoutCompletedToday;

    /**
     * Exercises for today's workout
     */
    private List<WorkoutBlockResponse> blocks;

    /**
     * Message to display (e.g., "Rest day", "No active assignment")
     */
    private String message;
}
