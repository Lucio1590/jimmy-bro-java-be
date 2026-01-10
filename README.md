## Gimmy-Bro Java Backend

Backend API for the Gimmy-Bro fitness application, built with Java 21 and Spring Boot 3.3. The system supports user authentication, exercise discovery, workout plan management, and workout execution logging.

---

## Environment File (read first)

Please take the `.env` file submitted on the university platform and place it in the project root. The application will not start without those values. Do not commit the provided `.env` to version control.

---

## Prerequisites

- Java 21 (`java -version`)
- Docker (`docker --version`)
- Maven (optional; the wrapper `./mvnw` is included)

---

## Quick Start

1. Place the provided `.env` in the repository root.
2. Start PostgreSQL via Docker:

```bash
docker compose up -d
```

Expected outcome:

```
[+] Running 3/3
 ✔ Network jimmy-bro-java-be_gymmy-network   Created   0.0s
 ✔ Volume "jimmy-bro-java-be_postgres_data"  Created   0.0s
 ✔ Container gymmy-bro-db                    Started   0.0s
```

3. Run the application:

```bash
./mvnw spring-boot:run
```

Note: On Windows, use `mvnw.cmd` instead of `./mvnw`. (i only used it on mac, this comes from mvnw documentation)
./mvnw will download dependencies on the first run. it is used to avoid requiring a local Maven installation.

4. Health check:

```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

---

## Project Description

The backend exposes REST APIs for managing a gym training workflow:
- Authentication and role-based access (Admin, Personal Trainer, Trainee).
- Exercise catalog with filters by body part, target muscle, and equipment.
- Workout plan creation, duplication, assignment to trainees, and versioning.
- Workout execution logging with sets, reps, weight, RPE, notes, and session completion.
- Basic statistics and history retrieval for trainees and trainers.

---

## Architecture

Layered Spring Boot application:
- Presentation: REST controllers under `presentation/controller`.
- Application: services and DTOs under `application/service` and `application/dto`.
- Domain: entities and aggregates under `domain` (users, tokens, workout structures).
- Infrastructure: security, external clients, configuration (Cloudinary, ExerciseDB, JWT) under `infrastructure` and `config`.
- Exception: centralized error handling under `exception`.

---

## Default Users (Flyway seed)

| Email | Password | Role |
|-------|----------|------|
| admin@gimmy.com | admin_password | ADMIN |
| pt@gimmy.com | pt_password | PT |
| trainee@gimmy.com | trainee_password | TRAINEE |

---

## Postman Usage

1. Import the collection file `Gimmy-Bro_API.postman_collection.json`.
2. Create a Postman environment with:
   - `baseUrl` = `http://localhost:8080`
   - `bearerToken` = empty (populated after login)
   - `refreshToken` = empty (populated after login)
3. Run `Auth > Login` to obtain tokens; subsequent requests automatically reuse `{{bearerToken}}`.

---

## Endpoint Overview (from Postman collection)

Auth
- POST /api/v1/auth/register — register a user (body: email, password, fullName, role).
- POST /api/v1/auth/token — login; saves access and refresh tokens in Postman env.
- POST /api/v1/auth/refresh — refresh tokens using `refreshToken`.
- POST /api/v1/auth/logout — revoke tokens (bearer required).
- GET /api/v1/auth/me — current authenticated user (bearer).

Users
- GET /api/v1/users — paginated list (admin).
- GET /api/v1/users/trainees — trainees of the current PT.
- GET /api/v1/users/:id — user detail by id.
- PATCH /api/v1/users/:id/status — activate/deactivate (admin, body: isActive).
- POST /api/v1/users/assignments — assign trainee to PT (body: ptId, traineeId).
- DELETE /api/v1/users/assignments/:traineeId — remove assignment.
- POST /api/v1/users/:id/profile-image — upload profile image (form-data file `image`).
- DELETE /api/v1/users/:id/profile-image — delete profile image.

Exercises
- GET /api/v1/exercises — search with optional `limit`, `offset`, `query`.
- GET /api/v1/exercises/:externalId — exercise detail by external id.
- GET /api/v1/exercises/bodyPart/:bodyPart — filter by body part.
- GET /api/v1/exercises/target/:target — filter by target muscle.
- GET /api/v1/exercises/equipment/:equipment — filter by equipment.
- GET /api/v1/exercises/metadata/body-parts — list body parts.
- GET /api/v1/exercises/metadata/categories — list categories.
- GET /api/v1/exercises/metadata/targets — list targets.
- GET /api/v1/exercises/metadata/equipment — list equipment.

Workout Plans
- POST /api/v1/workout-plans — create plan with days and exercises.
- GET /api/v1/workout-plans — list plans (paginated).
- GET /api/v1/workout-plans/my-plans — plans created by current PT.
- GET /api/v1/workout-plans/:id — plan detail.
- PUT /api/v1/workout-plans/:id — update name/description.
- DELETE /api/v1/workout-plans/:id — remove plan.
- POST /api/v1/workout-plans/:id/duplicate — duplicate plan.
- POST /api/v1/workout-plans/:id/assign — assign plan to trainee (body: traineeId, startDayNumber).
- DELETE /api/v1/workout-plans/:id/assignments/:traineeId — unassign plan.
- GET /api/v1/workout-plans/:id/assignments — list assignments.

Workout Execution
- GET /api/v1/workouts/active — current active workout for user.
- POST /api/v1/workouts/logs — start session (body: workoutDayId).
- POST /api/v1/workouts/logs/:logId/entries — add exercise entry (weight, reps, rpe, etc.).
- PATCH /api/v1/workouts/logs/:logId/complete — complete session (body: notes, rating).
- GET /api/v1/workouts/logs/:logId — fetch a specific log.
- GET /api/v1/workouts/history — history for current user (paginated).
- GET /api/v1/workouts/history/:traineeId — history for a trainee (PT/Admin).
- GET /api/v1/workouts/stats — stats for current user.
- GET /api/v1/workouts/stats/:traineeId — stats for a trainee (PT/Admin).

Swagger UI: http://localhost:8080/swagger-ui.html

---

## Troubleshooting

- Connection refused: ensure PostgreSQL is running (`docker compose ps`); restart if needed (`docker compose restart`).
- JWT signature invalid: set `JWT_SECRET` to at least 64 characters and reload environment variables.
- Full reset: `docker compose down -v && docker compose up -d && ./mvnw spring-boot:run`.
