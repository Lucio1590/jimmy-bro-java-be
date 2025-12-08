package com.gymmybro.domain.user;

/**
 * User roles in the Gimmy-Bro system.
 * Implements a hierarchical permission structure.
 */
public enum UserRole {
    /**
     * Full system access, manage all users, view all data,
     * trigger exercise ingestion, send custom emails
     */
    ADMIN,

    /**
     * Personal Trainer - Create/manage workout plans,
     * assign/unassign trainees, view trainee progress
     */
    PT,

    /**
     * Trainee - View assigned workouts, log workout execution,
     * view own history
     */
    TRAINEE
}
