package com.gymmybro.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Cloudinary image upload service.
 */
@Configuration
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "cloudinary.api-key")
@lombok.extern.slf4j.Slf4j
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        // Sanitize inputs to remove accidental whitespace
        String sanitizedCloudName = (cloudName != null) ? cloudName.trim() : null;
        String sanitizedApiKey = (apiKey != null) ? apiKey.trim() : null;
        String sanitizedApiSecret = (apiSecret != null) ? apiSecret.trim() : null;

        log.info("Initializing Cloudinary with cloud-name: '{}' (length: {})", sanitizedCloudName,
                (sanitizedCloudName != null ? sanitizedCloudName.length() : "null"));
        log.info("Cloudinary API Key length: {}", (sanitizedApiKey != null ? sanitizedApiKey.length() : "null"));

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", sanitizedCloudName,
                "api_key", sanitizedApiKey,
                "api_secret", sanitizedApiSecret,
                "secure", true));
    }
}
