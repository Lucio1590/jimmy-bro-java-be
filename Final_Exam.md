# Backend Programming Final Exam Project: Gymmy Bro

This document outlines the requirements and constraints for the "Gymmy Bro" backend final project.

## 1. Project Theme

**Gymmy Bro**: A management platform for Personal Trainers (PTs) and Trainees. PTs create workout plans and assign them to their trainees. Trainees log their workouts.

## 2. Entities & Domain Model

The domain model must include at least **eight tables** with coherent relationships.

- **Tables Required**: `users`, `workout_plans`, `workout_days`, `workout_blocks`, `workout_exercises`, `workout_logs`, `workout_log_entries`, `assignments`, `refresh_tokens`.
- **Inheritance**: Must be implemented (e.g., `User` → `PersonalTrainer`, `Trainee`, `Admin`).
- **Database Constraints**:
  - `users.email` must be unique.
  - `assignments` must enforce one active assignment per trainee.
  - **Indices**: Add indices on frequently filtered columns (e.g., `user.role`, `workout_log.date`).

## 3. User Management

A complete user management system is required.

- **Registration**: Separate flows or roles for PTs and Trainees.
- **Profile Image**: Users can update their profile picture.
  - **Security**: Uploaded files must be renamed to a randomized **UUID**.
  - **Validation**: Accept only image content-types (e.g., `image/png`, `image/jpeg`).
  - **Safety**: Prevent path traversal attacks.

## 4. Authentication & Authorization

Security must be implemented using **JWT** (Access + Refresh Tokens).

- **Roles**: `ADMIN`, `PT`, `TRAINEE`.
- **Ownership Enforcement**:
  - **PTs** can only modify/delete **Workout Plans** they created.
  - **PTs** can only view/manage **Trainees** assigned to them.
  - **Trainees** can only view their own assignments and create their own logs.
  - **Admins** have global access to all resources.

## 5. REST APIs

Expose consistent JSON REST APIs.

- Use **DTOs** for all inputs and outputs (never expose entities directly).
- **Error Handling**: Implement a `GlobalExceptionHandler` returning structured JSON errors (timestamp, status, message).
- **Validation**: Use Bean Validation (`@NotNull`, `@Email`, etc.) on all inputs.

## 6. Queries & Data Manipulation

- **Aggregations**: Implement complex queries (e.g., counting Trainees per PT, filtering Plans by difficulty).
- **Pagination**: All list endpoints must support pagination.
- **Time Handling**: Use **UTC** (`Instant`) for all server-side timestamps.

## 7. 3rd Party Integrations

Integrate at least two external APIs.

1. **ExerciseDB**: For fetching exercise metadata and GIFs.
2. **Cloudinary**: For hosting profile images.

- **Graceful Fallback**: The application **must start** even if API keys are missing. In such cases, features dependent on these APIs should degrade gracefully (e.g., return mock data or specific error codes) rather than crashing the server.

## 8. Supporting Material

- **Git Repository**: Must include `pom.xml`, `application.yml` (using env vars), and this `README`.
- **Docker Compose** (Recommended): A `docker-compose.yml` file to spin up PostgreSQL and the containerized application.
- **Postman Collection**:
  - Must include a **"Happy Path"** folder performing a full flow: *Register PT -> Register Trainee -> Create Plan -> Assign Plan -> Log Workout*.
  - Must use **Pre-request Scripts** to automatically save and use JWT tokens from login responses.
