package com.gymmybro.application.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

/**
 * Interface for image storage operations.
 * Decouples the application from specific image storage providers (like
 * Cloudinary).
 */
public interface ImageStorageService {

    /**
     * Upload a profile image.
     *
     * @param file   The image file to upload
     * @param userId The user ID for generating a unique identifier or path
     * @return The URL of the uploaded image
     */
    String uploadProfileImage(MultipartFile file, UUID userId);

    /**
     * Delete a profile image.
     *
     * @param userId The user ID whose profile image should be deleted
     */
    void deleteProfileImage(UUID userId);
}
