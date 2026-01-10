# Entity-Relationship Diagram

```mermaid
erDiagram
    USER {
        UUID id PK
        string email
        string hashed_password
        string full_name
        string profile_image_url
        enum role
        boolean is_active
        boolean email_verified
        timestamp created_at
        timestamp updated_at
        UUID pt_id FK
    }
    REFRESH_TOKEN {
        UUID id PK
        UUID user_id FK
        string token_hash
        timestamp expires_at
        boolean is_revoked
        string device_info
        string ip_address
        timestamp created_at
    }
    WORKOUT_PLAN {
        UUID id PK
        string name
        text description
        UUID created_by_id FK
        int duration_weeks
        string difficulty_level
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }
    WORKOUT_DAY {
        UUID id PK
        UUID workout_plan_id FK
        int day_number
        string name
        text description
        boolean is_rest_day
    }
    WORKOUT_BLOCK {
        UUID id PK
        UUID workout_day_id FK
        int block_order
        string block_type
        string name
        int block_sets
        int rest_between_rounds
    }
    WORKOUT_EXERCISE {
        UUID id PK
        UUID workout_block_id FK
        string exercise_external_id
        string exercise_name
        string exercise_gif_url
        int exercise_order
        int sets
        string target_reps
        double target_weight
        int rest_seconds
        int duration_seconds
        string tempo
        text notes
    }
    WORKOUT_PLAN_ASSIGNMENT {
        UUID id PK
        UUID trainee_id FK
        UUID workout_plan_id FK
        UUID assigned_by_id FK
        date start_date
        date end_date
        boolean is_active
        timestamp assigned_at
    }
    WORKOUT_LOG {
        UUID id PK
        UUID trainee_id FK
        UUID workout_day_id FK
        date workout_date
        timestamp started_at
        timestamp completed_at
        int duration_minutes
        text notes
        int rating
        timestamp created_at
    }
    WORKOUT_LOG_ENTRY {
        UUID id PK
        UUID workout_log_id FK
        UUID workout_exercise_id FK
        int set_number
        int actual_reps
        double actual_weight
        int actual_duration_seconds
        int rpe
        boolean is_completed
        string notes
    }

    USER ||--o{ REFRESH_TOKEN : owns
    USER ||--o{ USER : supervises
    USER ||--o{ WORKOUT_PLAN : creates
    USER ||--o{ WORKOUT_PLAN_ASSIGNMENT : trainee
    USER ||--o{ WORKOUT_PLAN_ASSIGNMENT : assigned_by
    USER ||--o{ WORKOUT_LOG : logs

    WORKOUT_PLAN ||--o{ WORKOUT_DAY : contains
    WORKOUT_DAY ||--o{ WORKOUT_BLOCK : contains
    WORKOUT_BLOCK ||--o{ WORKOUT_EXERCISE : contains

    WORKOUT_PLAN_ASSIGNMENT }o--|| WORKOUT_PLAN : assigned

    WORKOUT_LOG }o--|| WORKOUT_DAY : references
    WORKOUT_LOG ||--o{ WORKOUT_LOG_ENTRY : includes
    WORKOUT_LOG_ENTRY }o--|| WORKOUT_EXERCISE : performed_from
```

Notes:
- `role` enumerates ADMIN, PT, TRAINEE; `pt_id` on USER links a trainee to their personal trainer (self-join via `supervises`).
- `exercise_external_id` points to ExerciseDB records; names/GIF URLs are cached for speed.

## Entity details
- **Users**: Single-table inheritance via discriminator `role`. Base fields for auth/profile; `pt_id` ties a trainee to a personal trainer (PT sees trainees list).
- **RefreshToken**: Hashed refresh token per user with expiry/revocation plus device/IP metadata.
- **WorkoutPlan**: Authored by a user (PT/Admin); name, description, duration/difficulty, active flag; owns ordered WorkoutDays and assignments.
- **WorkoutDay**: Part of a plan; day number, optional name/description, rest flag; owns ordered WorkoutBlocks.
- **WorkoutBlock**: Part of a day; block order/type (NORMAL/SUPERSET/etc.), optional name, block sets/rest; owns ordered WorkoutExercises.
- **WorkoutExercise**: Part of a block; references ExerciseDB by `exercise_external_id` with cached name/GIF; order plus prescription fields (sets/reps/weight/rest/duration/tempo/notes).
- **WorkoutPlanAssignment**: Junction linking trainee to plan with assigning user, start/end dates, active flag; unique on trainee+plan.
- **WorkoutLog**: A trainee session for a given WorkoutDay/date; tracks start/end, duration, notes, rating; owns entries.
- **WorkoutLogEntry**: Set-level actuals tied to a WorkoutLog and prescribed WorkoutExercise; records set number, reps/weight/duration, RPE, completion, notes.
