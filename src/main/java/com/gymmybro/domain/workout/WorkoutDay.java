package com.gymmybro.domain.workout;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * WorkoutDay entity representing a single training day within a workout plan.
 */
@Entity
@Table(name = "workout_days")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutDay {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    /**
     * Day number within the program (1, 2, 3, etc.)
     */
    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    /**
     * Optional name for the day (e.g., "Push Day", "Leg Day")
     */
    private String name;

    /**
     * Optional description or notes for this day
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Whether this is a rest day
     */
    @Column(name = "is_rest_day")
    @Builder.Default
    private boolean isRestDay = false;

    @OneToMany(mappedBy = "workoutDay", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("blockOrder ASC")
    @Builder.Default
    private List<WorkoutBlock> workoutBlocks = new ArrayList<>();
}
