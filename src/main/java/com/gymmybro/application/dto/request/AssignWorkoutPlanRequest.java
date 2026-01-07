package com.gymmybro.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for assigning a workout plan to a trainee.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignWorkoutPlanRequest {

    @NotNull(message = "Trainee ID is required")
    private UUID traineeId;

    /**
     * Optional start date (defaults to today if not specified)
     */
    private LocalDate startDate;

    /**
     * Optional end date
     */
    private LocalDate endDate;
}
