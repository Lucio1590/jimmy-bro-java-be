package com.gymmybro.config;

import com.gymmybro.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the application.
 * Configures JWT authentication with role-based access control.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/exercises/metadata/**").permitAll()

                        // Auth endpoints - public
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/token", "/api/v1/auth/refresh")
                        .permitAll()
                        .requestMatchers("/api/v1/auth/forgot-password", "/api/v1/auth/reset-password").permitAll()
                        .requestMatchers("/api/v1/auth/confirm-email", "/api/v1/auth/resend-confirmation").permitAll()

                        // Admin only endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/exercises/ingest/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/emails/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/status").hasRole("ADMIN")

                        // PT only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/workout-plans").hasAnyRole("ADMIN", "PT")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/workout-plans/*").hasAnyRole("ADMIN", "PT")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/workout-plans/*").hasAnyRole("ADMIN", "PT")
                        .requestMatchers("/api/v1/workout-plans/*/assign").hasAnyRole("ADMIN", "PT")
                        .requestMatchers("/api/v1/users/assignments").hasAnyRole("ADMIN", "PT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/trainees").hasAnyRole("ADMIN", "PT")

                        // Authenticated endpoints - any logged in user
                        .requestMatchers("/api/v1/auth/me", "/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/exercises/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/workout-plans/**").authenticated()
                        .requestMatchers("/api/v1/workouts/**").authenticated()
                        .requestMatchers("/api/v1/users/*/profile-image").authenticated()

                        // For development: temporarily allow all (remove in production)
                        // .anyRequest().permitAll()

                        // Production: require authentication for any other request
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
