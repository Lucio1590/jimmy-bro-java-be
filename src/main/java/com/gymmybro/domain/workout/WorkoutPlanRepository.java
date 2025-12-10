package com.gymmybro.domain.workout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for WorkoutPlan entity operations.
 */
@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID> {

    /**
     * Find all plans created by a specific user (PT)
     */
    Page<WorkoutPlan> findByCreatedByIdAndIsActiveTrue(UUID creatorId, Pageable pageable);

    /**
     * Find all active plans
     */
    Page<WorkoutPlan> findByIsActiveTrue(Pageable pageable);

    /**
     * Search plans by name
     */
    @Query("SELECT wp FROM WorkoutPlan wp WHERE LOWER(wp.name) LIKE LOWER(CONCAT('%', :name, '%')) AND wp.isActive = true")
    Page<WorkoutPlan> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * Find plans by difficulty level
     */
    Page<WorkoutPlan> findByDifficultyLevelAndIsActiveTrue(String difficultyLevel, Pageable pageable);

    /**
     * Complex query: filter by multiple criteria
     */
    @Query("SELECT wp FROM WorkoutPlan wp WHERE " +
            "(:creatorId IS NULL OR wp.createdBy.id = :creatorId) AND " +
            "(:difficultyLevel IS NULL OR wp.difficultyLevel = :difficultyLevel) AND " +
            "(:name IS NULL OR LOWER(wp.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "wp.isActive = true")
    Page<WorkoutPlan> findByFilters(
            @Param("creatorId") UUID creatorId,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("name") String name,
            Pageable pageable);

    /**
     * Count plans by creator
     */
    long countByCreatedByIdAndIsActiveTrue(UUID creatorId);

    /**
     * Get all distinct difficulty levels
     */
    @Query("SELECT DISTINCT wp.difficultyLevel FROM WorkoutPlan wp WHERE wp.difficultyLevel IS NOT NULL")
    List<String> findAllDifficultyLevels();
}
