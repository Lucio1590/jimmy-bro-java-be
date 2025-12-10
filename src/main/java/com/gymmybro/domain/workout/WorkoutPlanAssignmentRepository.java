package com.gymmybro.domain.workout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for WorkoutPlanAssignment entity operations.
 */
@Repository
public interface WorkoutPlanAssignmentRepository extends JpaRepository<WorkoutPlanAssignment, UUID> {

    /**
     * Find all assignments for a trainee
     */
    List<WorkoutPlanAssignment> findByTraineeId(UUID traineeId);

    /**
     * Find active assignment for a trainee
     */
    Optional<WorkoutPlanAssignment> findByTraineeIdAndIsActiveTrue(UUID traineeId);

    /**
     * Find all assignments for a workout plan
     */
    List<WorkoutPlanAssignment> findByWorkoutPlanId(UUID workoutPlanId);

    /**
     * Find assignments by PT (assigned by)
     */
    Page<WorkoutPlanAssignment> findByAssignedById(UUID ptId, Pageable pageable);

    /**
     * Check if trainee is assigned to a specific plan
     */
    boolean existsByTraineeIdAndWorkoutPlanId(UUID traineeId, UUID workoutPlanId);

    /**
     * Find current assignments (active and within date range)
     */
    @Query("SELECT wpa FROM WorkoutPlanAssignment wpa WHERE " +
            "wpa.trainee.id = :traineeId AND " +
            "wpa.isActive = true AND " +
            "(wpa.startDate IS NULL OR wpa.startDate <= :today) AND " +
            "(wpa.endDate IS NULL OR wpa.endDate >= :today)")
    Optional<WorkoutPlanAssignment> findCurrentAssignment(
            @Param("traineeId") UUID traineeId,
            @Param("today") LocalDate today);

    /**
     * Count active assignments for a trainee
     */
    long countByTraineeIdAndIsActiveTrue(UUID traineeId);

    /**
     * Count trainees assigned to a plan
     */
    long countByWorkoutPlanIdAndIsActiveTrue(UUID workoutPlanId);
}
