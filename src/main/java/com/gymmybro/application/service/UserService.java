package com.gymmybro.application.service;

import com.gymmybro.application.dto.request.AssignTraineeRequest;
import com.gymmybro.application.dto.request.UpdateStatusRequest;
import com.gymmybro.application.dto.response.PaginatedResponse;
import com.gymmybro.application.dto.response.UserResponse;
import com.gymmybro.domain.user.User;
import com.gymmybro.domain.user.UserRepository;
import com.gymmybro.domain.user.UserRole;
import com.gymmybro.exception.BadRequestException;
import com.gymmybro.exception.ForbiddenException;
import com.gymmybro.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for user management operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get all users with optional filters (Admin only).
     */
    public PaginatedResponse<UserResponse> getAllUsers(
            String search,
            UserRole role,
            Boolean isActive,
            Pageable pageable) {

        Page<User> usersPage;

        if (search != null && !search.isBlank()) {
            usersPage = userRepository.searchUsers(search.trim(), role, isActive, pageable);
        } else {
            usersPage = userRepository.findWithFilters(role, isActive, pageable);
        }

        List<UserResponse> content = usersPage.getContent().stream()
                .map(UserResponse::fromEntity)
                .toList();

        return PaginatedResponse.<UserResponse>builder()
                .content(content)
                .page(usersPage.getNumber())
                .size(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .first(usersPage.isFirst())
                .last(usersPage.isLast())
                .build();
    }

    /**
     * Get user by ID.
     */
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return UserResponse.fromEntity(user);
    }

    /**
     * Get trainees assigned to a PT.
     */
    public PaginatedResponse<UserResponse> getTrainees(UUID ptId, Pageable pageable) {
        // Verify the PT exists and has PT role
        User pt = userRepository.findById(ptId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ptId));

        if (pt.getRole() != UserRole.PT && pt.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only PTs can have trainees");
        }

        Page<User> traineesPage = userRepository.findByPersonalTrainerId(ptId, pageable);

        List<UserResponse> content = traineesPage.getContent().stream()
                .map(UserResponse::fromEntity)
                .toList();

        return PaginatedResponse.<UserResponse>builder()
                .content(content)
                .page(traineesPage.getNumber())
                .size(traineesPage.getSize())
                .totalElements(traineesPage.getTotalElements())
                .totalPages(traineesPage.getTotalPages())
                .first(traineesPage.isFirst())
                .last(traineesPage.isLast())
                .build();
    }

    /**
     * Update user active status (Admin only).
     */
    @Transactional
    public UserResponse updateUserStatus(UUID userId, UpdateStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Prevent deactivating yourself or other admins
        if (user.getRole() == UserRole.ADMIN) {
            throw new ForbiddenException("Cannot modify admin user status");
        }

        user.setActive(request.getIsActive());
        User savedUser = userRepository.save(user);

        log.info("Updated user {} status to active={}", userId, request.getIsActive());
        return UserResponse.fromEntity(savedUser);
    }

    /**
     * Assign a trainee to a PT.
     */
    @Transactional
    public UserResponse assignTrainee(AssignTraineeRequest request) {
        User pt = userRepository.findById(request.getPtId())
                .orElseThrow(() -> new ResourceNotFoundException("PT", "id", request.getPtId()));

        User trainee = userRepository.findById(request.getTraineeId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainee", "id", request.getTraineeId()));

        // Validate roles
        if (pt.getRole() != UserRole.PT && pt.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Target user is not a PT");
        }

        if (trainee.getRole() != UserRole.TRAINEE) {
            throw new BadRequestException("Only users with TRAINEE role can be assigned");
        }

        // Check if already assigned to another PT
        if (trainee.getPersonalTrainer() != null) {
            throw new BadRequestException("Trainee is already assigned to PT: " +
                    trainee.getPersonalTrainer().getFullName());
        }

        trainee.setPersonalTrainer(pt);
        User savedTrainee = userRepository.save(trainee);

        log.info("Assigned trainee {} to PT {}", trainee.getId(), pt.getId());
        return UserResponse.fromEntity(savedTrainee);
    }

    /**
     * Unassign a trainee from their PT.
     */
    @Transactional
    public UserResponse unassignTrainee(UUID traineeId, UUID requestingPtId) {
        User trainee = userRepository.findById(traineeId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee", "id", traineeId));

        if (trainee.getRole() != UserRole.TRAINEE) {
            throw new BadRequestException("User is not a trainee");
        }

        if (trainee.getPersonalTrainer() == null) {
            throw new BadRequestException("Trainee is not assigned to any PT");
        }

        // Verify the requesting PT owns this trainee (or is admin)
        User requestingUser = userRepository.findById(requestingPtId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestingPtId));

        if (requestingUser.getRole() != UserRole.ADMIN &&
                !trainee.getPersonalTrainer().getId().equals(requestingPtId)) {
            throw new ForbiddenException("You can only unassign your own trainees");
        }

        UUID previousPtId = trainee.getPersonalTrainer().getId();
        trainee.setPersonalTrainer(null);
        User savedTrainee = userRepository.save(trainee);

        log.info("Unassigned trainee {} from PT {}", traineeId, previousPtId);
        return UserResponse.fromEntity(savedTrainee);
    }

    /**
     * Update user profile image URL (placeholder - will integrate with Cloudinary).
     */
    @Transactional
    public UserResponse updateProfileImage(UUID userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setProfileImageUrl(imageUrl);
        User savedUser = userRepository.save(user);

        log.info("Updated profile image for user {}", userId);
        return UserResponse.fromEntity(savedUser);
    }

    /**
     * Delete user profile image (placeholder - will integrate with Cloudinary).
     */
    @Transactional
    public UserResponse deleteProfileImage(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String previousUrl = user.getProfileImageUrl();
        user.setProfileImageUrl(null);
        User savedUser = userRepository.save(user);

        log.info("Deleted profile image for user {} (was: {})", userId, previousUrl);
        return UserResponse.fromEntity(savedUser);
    }
}
