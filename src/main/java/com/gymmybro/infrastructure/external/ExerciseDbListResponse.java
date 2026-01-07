package com.gymmybro.infrastructure.external;

import com.gymmybro.application.dto.response.ExerciseDbApiResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ExerciseDbListResponse {
    private boolean success;
    private List<ExerciseDbApiResponse> data;
}
