package com.gymmybro.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for consistent error responses across all
 * controllers.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        /**
         * Handle ResourceNotFoundException (404)
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiError> handleResourceNotFound(
                        ResourceNotFoundException ex, HttpServletRequest request) {
                log.debug("Resource not found: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("NOT_FOUND")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        /**
         * Handle UnauthorizedException (401)
         */
        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiError> handleUnauthorized(
                        UnauthorizedException ex, HttpServletRequest request) {
                log.debug("Unauthorized access: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("UNAUTHORIZED")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        /**
         * Handle Spring Security AuthenticationException (401)
         */
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ApiError> handleAuthenticationException(
                        AuthenticationException ex, HttpServletRequest request) {
                log.debug("Authentication failed: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("UNAUTHORIZED")
                                .message("Invalid credentials")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        /**
         * Handle BadCredentialsException (401)
         */
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiError> handleBadCredentials(
                        BadCredentialsException ex, HttpServletRequest request) {
                log.debug("Bad credentials: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("UNAUTHORIZED")
                                .message("Invalid email or password")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        /**
         * Handle ForbiddenException (403)
         */
        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiError> handleForbidden(
                        ForbiddenException ex, HttpServletRequest request) {
                log.debug("Access forbidden: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.FORBIDDEN.value())
                                .error("FORBIDDEN")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        /**
         * Handle Spring Security AccessDeniedException (403)
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiError> handleAccessDenied(
                        AccessDeniedException ex, HttpServletRequest request) {
                log.debug("Access denied: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.FORBIDDEN.value())
                                .error("FORBIDDEN")
                                .message("You don't have permission to access this resource")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        /**
         * Handle ConflictException (409)
         */
        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ApiError> handleConflict(
                        ConflictException ex, HttpServletRequest request) {
                log.debug("Conflict: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.CONFLICT.value())
                                .error("CONFLICT")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        /**
         * Handle BadRequestException (400)
         */
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiError> handleBadRequest(
                        BadRequestException ex, HttpServletRequest request) {
                log.debug("Bad request: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("BAD_REQUEST")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Handle validation errors from @Valid (400)
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> handleValidationErrors(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {
                log.debug("Validation failed: {}", ex.getMessage());

                Map<String, String> fieldErrors = new HashMap<>();
                for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
                        fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
                }

                ApiError error = ApiError.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("VALIDATION_ERROR")
                                .message("Validation failed")
                                .path(request.getRequestURI())
                                .fieldErrors(fieldErrors)
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Handle constraint violation errors (400)
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiError> handleConstraintViolation(
                        ConstraintViolationException ex, HttpServletRequest request) {
                log.debug("Constraint violation: {}", ex.getMessage());

                Map<String, String> fieldErrors = new HashMap<>();
                ex.getConstraintViolations().forEach(violation -> {
                        String field = violation.getPropertyPath().toString();
                        fieldErrors.put(field, violation.getMessage());
                });

                ApiError error = ApiError.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("VALIDATION_ERROR")
                                .message("Validation failed")
                                .path(request.getRequestURI())
                                .fieldErrors(fieldErrors)
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Handle all other exceptions (500)
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiError> handleGenericException(
                        Exception ex, HttpServletRequest request) {
                log.error("Unexpected error occurred", ex);

                ApiError error = ApiError.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("INTERNAL_ERROR")
                                .message("An unexpected error occurred")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        /**
         * Handle malformed JSON or invalid request body format (400)
         */
        @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
        public ResponseEntity<ApiError> handleHttpMessageNotReadable(
                        org.springframework.http.converter.HttpMessageNotReadableException ex,
                        HttpServletRequest request) {
                log.debug("Malformed JSON request: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("BAD_REQUEST")
                                .message("Malformed JSON request or invalid data format")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Handle type mismatch errors (e.g. string instead of UUID in path) (400)
         */
        @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiError> handleTypeMismatch(
                        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex,
                        HttpServletRequest request) {
                log.debug("Type mismatch: {} should be of type {}", ex.getName(), ex.getRequiredType().getSimpleName());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("BAD_REQUEST")
                                .message(String.format("Invalid value for parameter '%s'", ex.getName()))
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Handle missing request parameters (400)
         */
        @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
        public ResponseEntity<ApiError> handleMissingParams(
                        org.springframework.web.bind.MissingServletRequestParameterException ex,
                        HttpServletRequest request) {
                log.debug("Missing parameter: {}", ex.getParameterName());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("BAD_REQUEST")
                                .message(String.format("Missing required parameter '%s'", ex.getParameterName()))
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Handle MaxUploadSizeExceededException (413)
         */
        @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
        public ResponseEntity<ApiError> handleMaxSizeException(
                        org.springframework.web.multipart.MaxUploadSizeExceededException ex,
                        HttpServletRequest request) {
                log.debug("Upload size exceeded: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                                .error("PAYLOAD_TOO_LARGE")
                                .message("File size exceeds the maximum allowed limit")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
        }

        /**
         * Handle ImageUploadException (502)
         */
        @ExceptionHandler(com.gymmybro.exception.ImageUploadException.class)
        public ResponseEntity<ApiError> handleImageUploadException(
                        com.gymmybro.exception.ImageUploadException ex, HttpServletRequest request) {
                log.error("Image upload failed: {}", ex.getMessage());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.BAD_GATEWAY.value())
                                .error("IMAGE_UPLOAD_FAILED")
                                .message(ex.getMessage()) // "Failed to upload image: Connection reset"
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
        }

        /**
         * Handle method not supported (405)
         */
        @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ApiError> handleMethodNotSupported(
                        org.springframework.web.HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
                log.debug("Method not supported: {}", ex.getMethod());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                                .error("METHOD_NOT_ALLOWED")
                                .message(String.format("Request method '%s' not supported for this endpoint",
                                                ex.getMethod()))
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
        }

        /**
         * Handle NoHandlerFoundException (404) for non-existent endpoints
         */
        @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
        public ResponseEntity<ApiError> handleNoHandlerFoundException(
                        org.springframework.web.servlet.NoHandlerFoundException ex, HttpServletRequest request) {
                log.debug("No handler found for request: {}", ex.getRequestURL());

                ApiError error = ApiError.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("NOT_FOUND")
                                .message("The requested endpoint does not exist")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
}
