package com.gymmybro.infrastructure.external;

import com.gymmybro.application.dto.response.ExerciseDbApiResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExerciseDbSingleResponse {
    private boolean success;
    private ExerciseDbApiResponse data;
}
