package com.gymmybro.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

/**
 * Repository for RevokedAccessToken entity operations.
 * Used for JWT token blacklisting.
 */
@Repository
public interface RevokedAccessTokenRepository extends JpaRepository<RevokedAccessToken, UUID> {

    /**
     * Check if a token (by JTI) has been revoked
     */
    boolean existsByJti(String jti);

    /**
     * Delete expired revoked tokens (cleanup job)
     */
    @Modifying
    @Query("DELETE FROM RevokedAccessToken rat WHERE rat.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") Instant now);

    /**
     * Count revoked tokens for a user
     */
    long countByUserId(UUID userId);
}
