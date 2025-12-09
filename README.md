# Gimmy-Bro API

Java Spring Boot backend for gym workout management.

## Tech Stack

- **Java 21** + **Spring Boot 3.3.6**
- **PostgreSQL 16** (Docker)
- **JWT** authentication
- **Cloudinary** (images), **Mailgun** (emails), **ExerciseDB** (exercises)

## Quick Start

```bash
# Start database
docker compose up -d postgres

# Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Default admin:** `admin@gymmybro.com` / `admin123`

## Entity Relationship Diagram

![ER Diagram](ER.png)

## API Docs

<http://localhost:8080/swagger-ui.html>
