package com.gymmybro.domain.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for WorkoutDay entity operations.
 */
@Repository
public interface WorkoutDayRepository extends JpaRepository<WorkoutDay, UUID> {

    /**
     * Find all days for a workout plan, ordered by day number
     */
    List<WorkoutDay> findByWorkoutPlanIdOrderByDayNumberAsc(UUID workoutPlanId);

    /**
     * Find a specific day by plan and day number
     */
    WorkoutDay findByWorkoutPlanIdAndDayNumber(UUID workoutPlanId, Integer dayNumber);

    /**
     * Count days in a plan
     */
    long countByWorkoutPlanId(UUID workoutPlanId);

    /**
     * Delete all days for a plan
     */
    void deleteByWorkoutPlanId(UUID workoutPlanId);
}
