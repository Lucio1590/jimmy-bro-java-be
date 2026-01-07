package com.gymmybro.domain.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for WorkoutLogEntry entity operations.
 * Tracks individual exercise performance within a workout.
 */
@Repository
public interface WorkoutLogEntryRepository extends JpaRepository<WorkoutLogEntry, UUID> {

        /**
         * Find all entries for a workout log
         */
        List<WorkoutLogEntry> findByWorkoutLogIdOrderBySetNumberAsc(UUID workoutLogId);

        /**
         * Find entries for a specific exercise within a log
         */
        List<WorkoutLogEntry> findByWorkoutLogIdAndWorkoutExerciseIdOrderBySetNumberAsc(UUID workoutLogId,
                        UUID workoutExerciseId);

        /**
         * Get max weight lifted for an exercise by a trainee (by external ID)
         */
        @Query("SELECT MAX(wle.actualWeight) FROM WorkoutLogEntry wle " +
                        "JOIN wle.workoutLog wl " +
                        "WHERE wl.trainee.id = :traineeId AND wle.workoutExercise.exerciseExternalId = :exerciseExternalId")
        Double getMaxWeightByTraineeAndExercise(@Param("traineeId") UUID traineeId,
                        @Param("exerciseExternalId") String exerciseExternalId);

        /**
         * Get total volume (weight * reps) for an exercise by a trainee (by external
         * ID)
         */
        @Query("SELECT SUM(wle.actualWeight * wle.actualReps) FROM WorkoutLogEntry wle " +
                        "JOIN wle.workoutLog wl " +
                        "WHERE wl.trainee.id = :traineeId AND wle.workoutExercise.exerciseExternalId = :exerciseExternalId")
        Double getTotalVolumeByTraineeAndExercise(@Param("traineeId") UUID traineeId,
                        @Param("exerciseExternalId") String exerciseExternalId);

        /**
         * Get average RPE for a trainee
         */
        @Query("SELECT AVG(wle.rpe) FROM WorkoutLogEntry wle " +
                        "JOIN wle.workoutLog wl " +
                        "WHERE wl.trainee.id = :traineeId AND wle.rpe IS NOT NULL")
        Double getAverageRpeByTrainee(@Param("traineeId") UUID traineeId);

        /**
         * Count completed sets for a workout log
         */
        long countByWorkoutLogIdAndIsCompletedTrue(UUID workoutLogId);

        /**
         * Delete all entries for a workout log
         */
        void deleteByWorkoutLogId(UUID workoutLogId);
}
