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
 * Repository for WorkoutLog entity operations.
 * Includes complex queries for workout history and statistics.
 */
@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, UUID> {

    /**
     * Find all logs for a trainee, ordered by date descending
     */
    Page<WorkoutLog> findByTraineeIdOrderByWorkoutDateDesc(UUID traineeId, Pageable pageable);

    /**
     * Find log for a specific date
     */
    Optional<WorkoutLog> findByTraineeIdAndWorkoutDate(UUID traineeId, LocalDate workoutDate);

    /**
     * Find logs within a date range
     */
    @Query("SELECT wl FROM WorkoutLog wl WHERE " +
            "wl.trainee.id = :traineeId AND " +
            "wl.workoutDate BETWEEN :startDate AND :endDate " +
            "ORDER BY wl.workoutDate DESC")
    List<WorkoutLog> findByTraineeIdAndDateRange(
            @Param("traineeId") UUID traineeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Count workouts in a date range
     */
    @Query("SELECT COUNT(wl) FROM WorkoutLog wl WHERE " +
            "wl.trainee.id = :traineeId AND " +
            "wl.workoutDate BETWEEN :startDate AND :endDate")
    long countByTraineeIdAndDateRange(
            @Param("traineeId") UUID traineeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Get average workout duration for a trainee
     */
    @Query("SELECT AVG(wl.durationMinutes) FROM WorkoutLog wl WHERE wl.trainee.id = :traineeId AND wl.durationMinutes IS NOT NULL")
    Double getAverageDurationByTraineeId(@Param("traineeId") UUID traineeId);

    /**
     * Get average workout rating for a trainee
     */
    @Query("SELECT AVG(wl.rating) FROM WorkoutLog wl WHERE wl.trainee.id = :traineeId AND wl.rating IS NOT NULL")
    Double getAverageRatingByTraineeId(@Param("traineeId") UUID traineeId);

    /**
     * Count total workouts for a trainee
     */
    long countByTraineeId(UUID traineeId);

    /**
     * Find most recent workout
     */
    Optional<WorkoutLog> findTopByTraineeIdOrderByWorkoutDateDesc(UUID traineeId);

    /**
     * Get workout count by day of week (aggregation query)
     */
    @Query(value = "SELECT EXTRACT(DOW FROM wl.workout_date) as dayOfWeek, COUNT(*) as count " +
            "FROM workout_logs wl " +
            "WHERE wl.trainee_id = :traineeId " +
            "GROUP BY EXTRACT(DOW FROM wl.workout_date) " +
            "ORDER BY dayOfWeek", nativeQuery = true)
    List<Object[]> getWorkoutCountByDayOfWeek(@Param("traineeId") UUID traineeId);
}
