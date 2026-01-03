package com.gymmybro.presentation.controller;

import com.gymmybro.application.dto.request.AssignTraineeRequest;
import com.gymmybro.application.dto.request.UpdateStatusRequest;
import com.gymmybro.application.dto.response.PaginatedResponse;
import com.gymmybro.application.dto.response.UserResponse;
import com.gymmybro.application.service.UserService;
import com.gymmybro.domain.user.User;
import com.gymmybro.domain.user.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for user management operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * List all users with optional filters (Admin only).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all users", description = "Get paginated list of users with optional filters. Admin only.", responses = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin only")
    })
    public ResponseEntity<PaginatedResponse<UserResponse>> getAllUsers(
            @Parameter(description = "Search by email or name") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by role") @RequestParam(required = false) UserRole role,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(userService.getAllUsers(search, role, isActive, pageable));
    }

    /**
     * Get trainees assigned to the current PT.
     */
    @GetMapping("/trainees")
    @PreAuthorize("hasAnyRole('ADMIN', 'PT')")
    @Operation(summary = "Get PT's trainees", description = "Get paginated list of trainees assigned to the authenticated PT.", responses = {
            @ApiResponse(responseCode = "200", description = "Trainees retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - PT or Admin only")
    })
    public ResponseEntity<PaginatedResponse<UserResponse>> getTrainees(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        return ResponseEntity.ok(userService.getTrainees(currentUser.getId(), pageable));
    }

    /**
     * Get user by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get details of a specific user.", responses = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID") @PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Update user status (Admin only).
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user status", description = "Enable or disable a user account. Admin only.", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied or cannot modify admin"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> updateUserStatus(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(userService.updateUserStatus(id, request));
    }

    /**
     * Assign trainee to PT.
     */
    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'PT')")
    @Operation(summary = "Assign trainee to PT", description = "Assign a trainee to a personal trainer.", responses = {
            @ApiResponse(responseCode = "200", description = "Trainee assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid assignment (wrong roles or already assigned)"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> assignTrainee(
            @Valid @RequestBody AssignTraineeRequest request) {
        return ResponseEntity.ok(userService.assignTrainee(request));
    }

    /**
     * Unassign trainee from PT.
     */
    @DeleteMapping("/assignments/{traineeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PT')")
    @Operation(summary = "Unassign trainee from PT", description = "Remove a trainee from their assigned PT.", responses = {
            @ApiResponse(responseCode = "200", description = "Trainee unassigned successfully"),
            @ApiResponse(responseCode = "400", description = "Trainee not assigned"),
            @ApiResponse(responseCode = "403", description = "Access denied - can only unassign own trainees"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> unassignTrainee(
            @Parameter(description = "Trainee ID to unassign") @PathVariable UUID traineeId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.unassignTrainee(traineeId, currentUser.getId()));
    }

    /**
     * Upload profile image (placeholder - requires Cloudinary).
     */
    @PostMapping("/{id}/profile-image")
    @Operation(summary = "Upload profile image", description = "Upload a profile image for the user. Currently a placeholder.", responses = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> uploadProfileImage(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        // TODO: Integrate with Cloudinary
        // For now, just return the user without changes
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Delete profile image.
     */
    @DeleteMapping("/{id}/profile-image")
    @Operation(summary = "Delete profile image", description = "Remove the profile image for a user.", responses = {
            @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> deleteProfileImage(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.deleteProfileImage(id));
    }
}
