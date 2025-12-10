package com.gymmybro.domain.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for WorkoutExercise entity operations.
 */
@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, UUID> {

    /**
     * Find all exercises in a block, ordered by exercise order
     */
    List<WorkoutExercise> findByWorkoutBlockIdOrderByExerciseOrderAsc(UUID workoutBlockId);

    /**
     * Find all exercises in a workout day
     */
    @Query("SELECT we FROM WorkoutExercise we " +
            "JOIN we.workoutBlock wb " +
            "WHERE wb.workoutDay.id = :workoutDayId " +
            "ORDER BY wb.blockOrder, we.exerciseOrder")
    List<WorkoutExercise> findByWorkoutDayId(@Param("workoutDayId") UUID workoutDayId);

    /**
     * Count exercises using a specific exercise ID
     */
    long countByExerciseId(Integer exerciseId);

    /**
     * Delete all exercises for a block
     */
    void deleteByWorkoutBlockId(UUID workoutBlockId);
}
