package com.gymmybro.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email.
     */
    boolean existsByEmail(String email);

    /**
     * Count users by role.
     */
    long countByRole(UserRole role);

    /**
     * Find trainees assigned to a specific PT.
     */
    List<User> findByPersonalTrainerId(UUID ptId);

    /**
     * Find trainees assigned to a specific PT with pagination.
     */
    Page<User> findByPersonalTrainerId(UUID ptId, Pageable pageable);

    /**
     * Find users by role with pagination.
     */
    Page<User> findByRole(UserRole role, Pageable pageable);

    /**
     * Find users by role and active status with pagination.
     */
    Page<User> findByRoleAndIsActive(UserRole role, boolean isActive, Pageable pageable);

    /**
     * Search users by email or name containing (case-insensitive).
     */
    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> searchUsers(
            @Param("search") String search,
            @Param("role") UserRole role,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    /**
     * Find all users with optional role and status filters.
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:role IS NULL OR u.role = :role) " +
            "AND (:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> findWithFilters(
            @Param("role") UserRole role,
            @Param("isActive") Boolean isActive,
            Pageable pageable);
}
