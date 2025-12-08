package com.gymmybro.domain.workout;

import com.gymmybro.domain.exercise.Exercise;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * WorkoutExercise entity representing an exercise within a workout block.
 * Contains the prescribed sets, reps, and other parameters.
 */
@Entity
@Table(name = "workout_exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_block_id", nullable = false)
    private WorkoutBlock workoutBlock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    /**
     * Order of this exercise within the block
     */
    @Column(name = "exercise_order", nullable = false)
    private Integer exerciseOrder;

    /**
     * Number of sets prescribed
     */
    private Integer sets;

    /**
     * Target reps (can be a range like "8-12" stored as string)
     */
    @Column(name = "target_reps")
    private String targetReps;

    /**
     * Target weight in kg (optional, trainee may adjust)
     */
    @Column(name = "target_weight")
    private Double targetWeight;

    /**
     * Rest time between sets in seconds
     */
    @Column(name = "rest_seconds")
    private Integer restSeconds;

    /**
     * Duration in seconds (for timed exercises like planks)
     */
    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    /**
     * Tempo notation (e.g., "3-1-2-0" for eccentric-pause-concentric-pause)
     */
    private String tempo;

    /**
     * Additional notes or instructions for this exercise
     */
    @Column(columnDefinition = "TEXT")
    private String notes;
}
