package com.gymmybro.domain.exercise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Exercise entity operations.
 * Includes complex queries for filtering and searching exercises.
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    /**
     * Find exercise by external ID (ExerciseDB ID)
     */
    Optional<Exercise> findByExternalId(String externalId);

    /**
     * Check if exercise exists by external ID
     */
    boolean existsByExternalId(String externalId);

    /**
     * Search exercises by name (case-insensitive, partial match)
     */
    @Query("SELECT e FROM Exercise e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Exercise> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * Find exercises by body part
     */
    Page<Exercise> findByBodyPartIgnoreCase(String bodyPart, Pageable pageable);

    /**
     * Find exercises by target muscle
     */
    Page<Exercise> findByTargetMuscleIgnoreCase(String targetMuscle, Pageable pageable);

    /**
     * Find exercises by equipment
     */
    Page<Exercise> findByEquipmentIgnoreCase(String equipment, Pageable pageable);

    /**
     * Find exercises by category
     */
    Page<Exercise> findByCategoryIgnoreCase(String category, Pageable pageable);

    /**
     * Complex query: filter by multiple criteria
     */
    @Query("SELECT e FROM Exercise e WHERE " +
            "(:bodyPart IS NULL OR LOWER(e.bodyPart) = LOWER(:bodyPart)) AND " +
            "(:targetMuscle IS NULL OR LOWER(e.targetMuscle) = LOWER(:targetMuscle)) AND " +
            "(:equipment IS NULL OR LOWER(e.equipment) = LOWER(:equipment)) AND " +
            "(:category IS NULL OR LOWER(e.category) = LOWER(:category)) AND " +
            "(:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Exercise> findByFilters(
            @Param("name") String name,
            @Param("bodyPart") String bodyPart,
            @Param("targetMuscle") String targetMuscle,
            @Param("equipment") String equipment,
            @Param("category") String category,
            Pageable pageable);

    /**
     * Get all distinct body parts
     */
    @Query("SELECT DISTINCT e.bodyPart FROM Exercise e WHERE e.bodyPart IS NOT NULL ORDER BY e.bodyPart")
    List<String> findAllBodyParts();

    /**
     * Get all distinct target muscles
     */
    @Query("SELECT DISTINCT e.targetMuscle FROM Exercise e WHERE e.targetMuscle IS NOT NULL ORDER BY e.targetMuscle")
    List<String> findAllTargetMuscles();

    /**
     * Get all distinct equipment types
     */
    @Query("SELECT DISTINCT e.equipment FROM Exercise e WHERE e.equipment IS NOT NULL ORDER BY e.equipment")
    List<String> findAllEquipment();

    /**
     * Get all distinct categories
     */
    @Query("SELECT DISTINCT e.category FROM Exercise e WHERE e.category IS NOT NULL ORDER BY e.category")
    List<String> findAllCategories();

    /**
     * Count exercises by body part
     */
    long countByBodyPartIgnoreCase(String bodyPart);
}
