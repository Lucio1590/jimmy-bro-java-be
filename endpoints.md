# API Endpoints

## Base Path

All paths below are relative to the server root.

## Authentication

**Controller**: `AuthController`
**Base Path**: `/api/v1/auth`

| Method | Path | Description | Access |
|--------|------|-------------|--------|
| POST | `/register` | Register a new user | Public |
| POST | `/token` | Login (get access/refresh tokens) | Public |
| POST | `/refresh` | Refresh access token | Public |
| POST | `/logout` | Logout (revoke tokens) | Public |
| GET | `/me` | Get current authenticated user | Authenticated |

## Exercises

**Controller**: `ExerciseController`
**Base Path**: `/api/v1/exercises`

| Method | Path | Description | Access |
|--------|------|-------------|--------|
| GET | `/` | List exercises (search by name) | Authenticated |
| GET | `/{externalId}` | Get exercise by ID | Authenticated |
| GET | `/bodyPart/{bodyPart}` | Filter by body part | Authenticated |
| GET | `/target/{target}` | Filter by target muscle | Authenticated |
| GET | `/equipment/{equipment}` | Filter by equipment | Authenticated |
| GET | `/metadata/body-parts` | Get list of body parts | Authenticated |
| GET | `/metadata/categories` | Get list of categories | Authenticated |
| GET | `/metadata/targets` | Get list of target muscles | Authenticated |
| GET | `/metadata/equipment` | Get list of equipment types | Authenticated |

## User Management

**Controller**: `UserController`
**Base Path**: `/api/v1/users`

| Method | Path | Description | Access |
|--------|------|-------------|--------|
| GET | `/` | List all users (filters available) | ADMIN |
| GET | `/trainees` | Get trainees assigned to current PT | ADMIN, PT |
| GET | `/{id}` | Get user by ID | Authenticated |
| PATCH | `/{id}/status` | Update user status (active/inactive) | ADMIN |
| POST | `/assignments` | Assign trainee to PT | ADMIN, PT |
| DELETE | `/assignments/{traineeId}` | Unassign trainee from PT | ADMIN, PT |
| POST | `/{id}/profile-image` | Upload profile image | Authenticated |
| DELETE | `/{id}/profile-image` | Delete profile image | Authenticated |

## Workout Plans

**Controller**: `WorkoutPlanController`
**Base Path**: `/api/v1/workout-plans`

| Method | Path | Description | Access |
|--------|------|-------------|--------|
| POST | `/` | Create workout plan | ADMIN, PT |
| GET | `/` | List workout plans | Authenticated |
| GET | `/mine` | Get plans created by current user | ADMIN, PT |
| GET | `/{id}` | Get workout plan details | Authenticated |
| PUT | `/{id}` | Update workout plan | ADMIN, PT |
| DELETE | `/{id}` | Delete (archive) workout plan | ADMIN, PT |
| POST | `/{id}/duplicate` | Duplicate workout plan | ADMIN, PT |
| POST | `/{id}/assign` | Assign plan to trainee | ADMIN, PT |
| DELETE | `/{id}/assignments/{traineeId}` | Unassign plan from trainee | ADMIN, PT |
| GET | `/{id}/assignments` | Get assignments for a plan | ADMIN, PT |

## Workout Execution

**Controller**: `WorkoutExecutionController`
**Base Path**: `/api/v1/workouts`

| Method | Path | Description | Access |
|--------|------|-------------|--------|
| GET | `/active` | Get today's active workout | TRAINEE |
| POST | `/logs` | Start workout session | TRAINEE |
| POST | `/logs/{logId}/entries` | Log an exercise set/entry | TRAINEE |
| PATCH | `/logs/{logId}/complete` | Mark workout as completed | TRAINEE |
| GET | `/logs/{logId}` | Get details of a specific log | TRAINEE, PT, ADMIN |
| GET | `/history` | Get own workout history | TRAINEE |
| GET | `/history/{traineeId}` | Get trainee's workout history | PT, ADMIN |
| GET | `/stats` | Get own workout statistics | TRAINEE |
| GET | `/stats/{traineeId}` | Get trainee's workout statistics | PT, ADMIN |
