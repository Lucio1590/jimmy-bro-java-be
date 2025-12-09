package com.gymmybro.infrastructure;

import com.gymmybro.domain.user.User;
import com.gymmybro.domain.user.UserRepository;
import com.gymmybro.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer runs on application startup to seed the database
 * with initial data if it's empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@gymmybro.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            log.info("Database is empty. Seeding initial data...");
            seedAdminUser();
            log.info("Initial data seeding completed.");
        } else {
            log.info("Database already contains data. Skipping initialization.");
        }
    }

    private void seedAdminUser() {
        User admin = User.builder()
                .email(adminEmail)
                .hashedPassword(passwordEncoder.encode(adminPassword))
                .fullName("System Administrator")
                .role(UserRole.ADMIN)
                .isActive(true)
                .emailVerified(true)
                .build();

        userRepository.save(admin);
        log.info("Created default admin user: {}", admin.getEmail());
    }
}
