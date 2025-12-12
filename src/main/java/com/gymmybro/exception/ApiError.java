package com.gymmybro.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Standard error response structure for API errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Error type (e.g., "NOT_FOUND", "VALIDATION_ERROR")
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Request path that caused the error
     */
    private String path;

    /**
     * Timestamp of the error
     */
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * Field-specific validation errors (optional)
     */
    private Map<String, String> fieldErrors;

    /**
     * List of error details for multiple errors (optional)
     */
    private List<String> details;
}
