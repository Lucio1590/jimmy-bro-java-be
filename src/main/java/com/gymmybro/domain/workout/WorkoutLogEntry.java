package com.gymmybro.domain.workout;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * WorkoutLogEntry entity representing the actual performance of an exercise
 * within a workout.
 * Tracks actual sets, reps, and weight performed by the trainee.
 */
@Entity
@Table(name = "workout_log_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_log_id", nullable = false)
    private WorkoutLog workoutLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_exercise_id", nullable = false)
    private WorkoutExercise workoutExercise;

    /**
     * Set number (1, 2, 3, etc.)
     */
    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    /**
     * Actual reps performed
     */
    @Column(name = "actual_reps")
    private Integer actualReps;

    /**
     * Actual weight used in kg
     */
    @Column(name = "actual_weight")
    private Double actualWeight;

    /**
     * Duration in seconds (for timed exercises)
     */
    @Column(name = "actual_duration_seconds")
    private Integer actualDurationSeconds;

    /**
     * Rate of Perceived Exertion (1-10)
     */
    private Integer rpe;

    /**
     * Whether this set was completed successfully
     */
    @Column(name = "is_completed")
    @Builder.Default
    private boolean isCompleted = true;

    /**
     * Notes for this specific set
     */
    private String notes;
}
