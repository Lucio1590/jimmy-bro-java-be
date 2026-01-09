package com.gymmybro.infrastructure.external;

import com.gymmybro.application.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Stub implementation of ImageStorageService.
 * Used when Cloudinary configuration is missing to ensure graceful startup.
 */
@Service
@ConditionalOnMissingBean(ImageStorageService.class)
@Slf4j
public class StubImageStorageService implements ImageStorageService {

    public StubImageStorageService() {
        log.warn("Cloudinary API key not found. Image storage will be disabled (Stub Mode).");
    }

    @Override
    public String uploadProfileImage(MultipartFile file, UUID userId) {
        log.warn("STUB: Simulated upload profile image for user {}", userId);
        return "https://placehold.co/400x400?text=No+Image+Storage";
    }

    @Override
    public void deleteProfileImage(UUID userId) {
        log.warn("STUB: Simulated delete profile image for user {}", userId);
    }
}
