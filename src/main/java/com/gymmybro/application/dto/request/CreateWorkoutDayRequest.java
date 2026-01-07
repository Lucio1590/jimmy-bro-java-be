package com.gymmybro.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a workout day within a plan.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkoutDayRequest {

    @NotNull(message = "Day number is required")
    @Min(value = 1, message = "Day number must be at least 1")
    private Integer dayNumber;

    /**
     * Optional name for the day (e.g., "Push Day", "Leg Day")
     */
    private String name;

    private String description;

    /**
     * Whether this is a rest day (no exercises)
     */
    @Builder.Default
    private boolean isRestDay = false;

    /**
     * Nested workout blocks (only required if not a rest day)
     */
    @Valid
    private List<CreateWorkoutBlockRequest> blocks;
}
