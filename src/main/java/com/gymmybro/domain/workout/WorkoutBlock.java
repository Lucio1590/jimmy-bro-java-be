package com.gymmybro.domain.workout;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * WorkoutBlock entity representing a group of exercises within a workout day.
 * Blocks can represent supersets, circuits, or regular exercise groups.
 */
@Entity
@Table(name = "workout_blocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_day_id", nullable = false)
    private WorkoutDay workoutDay;

    /**
     * Order of this block within the day
     */
    @Column(name = "block_order", nullable = false)
    private Integer blockOrder;

    /**
     * Type of block: NORMAL, SUPERSET, CIRCUIT, DROP_SET
     */
    @Column(name = "block_type", nullable = false)
    @Builder.Default
    private String blockType = "NORMAL";

    /**
     * Optional name for the block
     */
    private String name;

    /**
     * Number of rounds/sets for the entire block (for circuits)
     */
    @Column(name = "block_sets")
    private Integer blockSets;

    /**
     * Rest time between rounds in seconds
     */
    @Column(name = "rest_between_rounds")
    private Integer restBetweenRounds;

    @OneToMany(mappedBy = "workoutBlock", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("exerciseOrder ASC")
    @Builder.Default
    private List<WorkoutExercise> workoutExercises = new ArrayList<>();
}
