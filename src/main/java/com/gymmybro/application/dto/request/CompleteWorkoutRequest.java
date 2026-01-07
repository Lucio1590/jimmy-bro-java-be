package com.gymmybro.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for completing a workout session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteWorkoutRequest {

    /**
     * Overall notes for the workout session
     */
    private String notes;

    /**
     * Rating for the workout (1-10)
     */
    @Min(value = 1, message = "Rating must be between 1 and 10")
    @Max(value = 10, message = "Rating must be between 1 and 10")
    private Integer rating;
}
