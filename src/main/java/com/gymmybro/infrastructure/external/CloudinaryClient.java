package com.gymmybro.infrastructure.external;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.gymmybro.application.service.ImageStorageService;
import com.gymmybro.exception.BadRequestException;
import com.gymmybro.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Client for Cloudinary image upload service.
 * Handles profile image uploads and deletions.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "cloudinary.api-key")
public class CloudinaryClient implements ImageStorageService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:gymmy-bro/profiles}")
    private String profileFolder;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_CONTENT_TYPES = {
            "image/jpeg", "image/png", "image/gif", "image/webp"
    };

    /**
     * Upload a profile image to Cloudinary.
     *
     * @param file   The image file to upload
     * @param userId The user ID for generating a unique public ID
     * @return The URL of the uploaded image
     */
    @Override
    @SuppressWarnings("unchecked")
    public String uploadProfileImage(MultipartFile file, UUID userId) {
        validateFile(file);

        try {
            String publicId = profileFolder + "/" + userId.toString();

            // Fix: Use Transformation object instead of Map
            // This prevents "Invalid transformation component" errors
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "overwrite", true,
                            "resource_type", "image",
                            "transformation", new Transformation<>()
                                    .width(400)
                                    .height(400)
                                    .crop("fill")
                                    .gravity("face")
                                    .quality("auto")
                                    .fetchFormat("webp")));

            String url = (String) uploadResult.get("secure_url");
            log.info("Successfully uploaded profile image for user {} to {}", userId, url);
            return url;

        } catch (IOException e) {
            log.error("Failed to upload profile image for user {}: {}", userId, e.getMessage());
            throw new ImageUploadException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a profile image from Cloudinary.
     *
     * @param userId The user ID whose profile image should be deleted
     */
    @Override
    @SuppressWarnings("unchecked")
    public void deleteProfileImage(UUID userId) {
        try {
            String publicId = profileFolder + "/" + userId.toString();

            Map<String, Object> result = cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", "image"));

            String deleteResult = (String) result.get("result");
            if ("ok".equals(deleteResult)) {
                log.info("Successfully deleted profile image for user {}", userId);
            } else {
                log.warn("Profile image deletion returned: {} for user {}", deleteResult, userId);
            }

        } catch (IOException e) {
            log.error("Failed to delete profile image for user {}: {}", userId, e.getMessage());
            throw new ImageUploadException("Failed to delete image: " + e.getMessage(), e);
        }
    }

    /**
     * Validate the uploaded file.
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum allowed (5MB)");
        }

        String contentType = file.getContentType();
        boolean isValidType = false;
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType)) {
                isValidType = true;
                break;
            }
        }

        if (!isValidType) {
            throw new BadRequestException("Invalid file type. Allowed types: JPEG, PNG, GIF, WebP");
        }
    }

    /**
     * Extract public ID from a Cloudinary URL.
     * Useful for deleting images when you only have the URL.
     */
    public String extractPublicIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Extract path after /upload/ and before file extension
        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex == -1) {
            return null;
        }

        String path = url.substring(uploadIndex + 8); // Skip "/upload/"

        // Remove version number if present (e.g., "v1234567890/")
        if (path.startsWith("v") && path.contains("/")) {
            path = path.substring(path.indexOf("/") + 1);
        }

        // Remove file extension
        int lastDot = path.lastIndexOf(".");
        if (lastDot != -1) {
            path = path.substring(0, lastDot);
        }

        return path;
    }
}
