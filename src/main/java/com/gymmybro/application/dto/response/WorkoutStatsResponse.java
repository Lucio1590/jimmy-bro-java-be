package com.gymmybro.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * Response DTO for workout statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutStatsResponse {

    /**
     * Total workouts completed
     */
    private Long totalWorkouts;

    /**
     * Total workouts in the last 7 days
     */
    private Long workoutsThisWeek;

    /**
     * Total workouts in the last 30 days
     */
    private Long workoutsThisMonth;

    /**
     * Average workout duration in minutes
     */
    private Double averageDurationMinutes;

    /**
     * Average workout rating (1-10)
     */
    private Double averageRating;

    /**
     * Current streak (consecutive days with workouts)
     */
    private Integer currentStreak;

    /**
     * Longest streak ever
     */
    private Integer longestStreak;

    /**
     * Date of last workout
     */
    private LocalDate lastWorkoutDate;

    /**
     * Workouts by day of week (0=Sunday, 6=Saturday)
     */
    private Map<Integer, Long> workoutsByDayOfWeek;
}
