package com.gymmybro.infrastructure.external;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * Wrapper for generic list responses from ExerciseDB.
 * Used for endpoints that return {"data": [...]}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDbGenericListResponse {
    private List<ExerciseDbMetadataResponse> data;
}
