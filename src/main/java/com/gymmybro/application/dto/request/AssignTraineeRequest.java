package com.gymmybro.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for assigning a trainee to a PT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignTraineeRequest {

    @NotNull(message = "Trainee ID is required")
    private UUID traineeId;

    @NotNull(message = "PT ID is required")
    private UUID ptId;
}
