package com.gymmybro.domain.workout;

import com.gymmybro.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * WorkoutPlanAssignment entity representing the assignment of a workout plan to
 * a trainee.
 * Junction table for the many-to-many relationship between trainees and workout
 * plans.
 */
@Entity
@Table(name = "workout_plan_assignments", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "trainee_id", "workout_plan_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private User trainee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    /**
     * The PT who assigned this plan
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_id", nullable = false)
    private User assignedBy;

    /**
     * Date when the trainee should start this plan
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * Date when this assignment ends (optional)
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Whether this is the trainee's currently active plan
     */
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private Instant assignedAt;
}
