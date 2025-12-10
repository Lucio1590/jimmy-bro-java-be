package com.gymmybro.domain.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for WorkoutBlock entity operations.
 */
@Repository
public interface WorkoutBlockRepository extends JpaRepository<WorkoutBlock, UUID> {

    /**
     * Find all blocks for a workout day, ordered by block order
     */
    List<WorkoutBlock> findByWorkoutDayIdOrderByBlockOrderAsc(UUID workoutDayId);

    /**
     * Count blocks in a day
     */
    long countByWorkoutDayId(UUID workoutDayId);

    /**
     * Delete all blocks for a day
     */
    void deleteByWorkoutDayId(UUID workoutDayId);
}
