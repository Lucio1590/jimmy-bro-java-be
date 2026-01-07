package com.gymmybro.infrastructure.external;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a single metadata item from ExerciseDB (e.g., a body part or
 * equipment).
 * The API returns objects like {"name": "chest"}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDbMetadataResponse {
    private String name;
}
