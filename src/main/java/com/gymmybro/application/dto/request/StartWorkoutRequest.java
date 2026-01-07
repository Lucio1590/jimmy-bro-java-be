package com.gymmybro.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for starting a new workout session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartWorkoutRequest {

    @NotNull(message = "Workout day ID is required")
    private UUID workoutDayId;

    /**
     * Optional date override (defaults to today)
     */
    private LocalDate workoutDate;
}
