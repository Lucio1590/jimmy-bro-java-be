-- Flyway Migration V1: Initial Schema
-- Creates all tables for Gymmy-Bro workout management system

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- 1. USERS TABLE
-- =====================================================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    hashed_password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    profile_image_url VARCHAR(500),
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    pt_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for PT-Trainee relationship queries
CREATE INDEX idx_users_pt_id ON users(pt_id);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_email ON users(email);

-- =====================================================
-- 2. REFRESH TOKENS TABLE
-- =====================================================
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE,
    device_info VARCHAR(255),
    ip_address VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

-- =====================================================
-- 3. REVOKED ACCESS TOKENS TABLE
-- =====================================================
CREATE TABLE revoked_access_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    jti VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id UUID,
    revocation_reason VARCHAR(255),
    revoked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_revoked_tokens_jti ON revoked_access_tokens(jti);
CREATE INDEX idx_revoked_tokens_expires_at ON revoked_access_tokens(expires_at);

-- =====================================================
-- 4. EXERCISES TABLE
-- =====================================================
CREATE TABLE exercises (
    id SERIAL PRIMARY KEY,
    external_id VARCHAR(100) UNIQUE,
    name VARCHAR(255) NOT NULL,
    target_muscle VARCHAR(100),
    body_part VARCHAR(100),
    category VARCHAR(50) NOT NULL,
    equipment VARCHAR(100),
    gif_url VARCHAR(500),
    extra_data JSONB
);

CREATE INDEX idx_exercises_name ON exercises(name);
CREATE INDEX idx_exercises_body_part ON exercises(body_part);
CREATE INDEX idx_exercises_target_muscle ON exercises(target_muscle);
CREATE INDEX idx_exercises_category ON exercises(category);

-- =====================================================
-- 5. WORKOUT PLANS TABLE
-- =====================================================
CREATE TABLE workout_plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    duration_weeks INTEGER,
    difficulty_level VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workout_plans_created_by ON workout_plans(created_by_id);
CREATE INDEX idx_workout_plans_active ON workout_plans(is_active);

-- =====================================================
-- 6. WORKOUT DAYS TABLE
-- =====================================================
CREATE TABLE workout_days (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workout_plan_id UUID NOT NULL REFERENCES workout_plans(id) ON DELETE CASCADE,
    day_number INTEGER NOT NULL,
    name VARCHAR(255),
    description TEXT,
    is_rest_day BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_workout_days_plan ON workout_days(workout_plan_id);

-- =====================================================
-- 7. WORKOUT BLOCKS TABLE
-- =====================================================
CREATE TABLE workout_blocks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workout_day_id UUID NOT NULL REFERENCES workout_days(id) ON DELETE CASCADE,
    block_order INTEGER NOT NULL,
    block_type VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
    name VARCHAR(255),
    block_sets INTEGER,
    rest_between_rounds INTEGER
);

CREATE INDEX idx_workout_blocks_day ON workout_blocks(workout_day_id);

-- =====================================================
-- 8. WORKOUT EXERCISES TABLE
-- =====================================================
CREATE TABLE workout_exercises (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workout_block_id UUID NOT NULL REFERENCES workout_blocks(id) ON DELETE CASCADE,
    exercise_id INTEGER NOT NULL REFERENCES exercises(id) ON DELETE RESTRICT,
    exercise_order INTEGER NOT NULL,
    sets INTEGER,
    target_reps VARCHAR(50),
    target_weight DOUBLE PRECISION,
    rest_seconds INTEGER,
    duration_seconds INTEGER,
    tempo VARCHAR(20),
    notes TEXT
);

CREATE INDEX idx_workout_exercises_block ON workout_exercises(workout_block_id);
CREATE INDEX idx_workout_exercises_exercise ON workout_exercises(exercise_id);

-- =====================================================
-- 9. WORKOUT PLAN ASSIGNMENTS TABLE
-- =====================================================
CREATE TABLE workout_plan_assignments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trainee_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    workout_plan_id UUID NOT NULL REFERENCES workout_plans(id) ON DELETE CASCADE,
    assigned_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(trainee_id, workout_plan_id)
);

CREATE INDEX idx_assignments_trainee ON workout_plan_assignments(trainee_id);
CREATE INDEX idx_assignments_plan ON workout_plan_assignments(workout_plan_id);
CREATE INDEX idx_assignments_active ON workout_plan_assignments(is_active);

-- =====================================================
-- 10. WORKOUT LOGS TABLE
-- =====================================================
CREATE TABLE workout_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trainee_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    workout_day_id UUID NOT NULL REFERENCES workout_days(id) ON DELETE CASCADE,
    workout_date DATE NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    duration_minutes INTEGER,
    notes TEXT,
    rating INTEGER CHECK (rating >= 1 AND rating <= 10),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workout_logs_trainee ON workout_logs(trainee_id);
CREATE INDEX idx_workout_logs_date ON workout_logs(workout_date);
CREATE INDEX idx_workout_logs_day ON workout_logs(workout_day_id);

-- =====================================================
-- 11. WORKOUT LOG ENTRIES TABLE
-- =====================================================
CREATE TABLE workout_log_entries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workout_log_id UUID NOT NULL REFERENCES workout_logs(id) ON DELETE CASCADE,
    workout_exercise_id UUID NOT NULL REFERENCES workout_exercises(id) ON DELETE CASCADE,
    set_number INTEGER NOT NULL,
    actual_reps INTEGER,
    actual_weight DOUBLE PRECISION,
    actual_duration_seconds INTEGER,
    rpe INTEGER CHECK (rpe >= 1 AND rpe <= 10),
    is_completed BOOLEAN DEFAULT TRUE,
    notes VARCHAR(500)
);

CREATE INDEX idx_log_entries_log ON workout_log_entries(workout_log_id);
CREATE INDEX idx_log_entries_exercise ON workout_log_entries(workout_exercise_id);
