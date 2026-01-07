# 🏋️ Gimmy-Bro Java Backend

Backend API per l'applicazione fitness Gimmy-Bro, sviluppato con **Java 21** e **Spring Boot 3.3**.

---

## 🚀 Quick Start (5 minuti)

### 1️⃣ Prerequisiti

- **Java 21** (`java -version`)
- **Docker** (`docker --version`)
- **Maven** (opzionale, incluso wrapper `./mvnw`)

### 2️⃣ Configura le variabili d'ambiente

```bash
# Copia il file di esempio
cp .env.example .env

# Modifica .env con i tuoi valori (obbligatori in grassetto)
```

| Variabile | Descrizione | Obbligatorio |
|-----------|-------------|:------------:|
| `JWT_SECRET` | Chiave segreta JWT (min 64 caratteri) | ✅ |
| `JWT_REFRESH_SECRET` | Chiave per refresh token | ✅ |
| `DB_PASSWORD` | Password PostgreSQL | ✅ |
| `RAPIDAPI_KEY` | API key per ExerciseDB | ❌ |
| `CLOUDINARY_*` | Configurazione Cloudinary | ❌ |
| `MAILGUN_*` | Configurazione Mailgun | ❌ |

> 💡 **Tip**: Genera chiavi JWT sicure con: `openssl rand -base64 64`

### 3️⃣ Avvia PostgreSQL con Docker

```bash
# Avvia il database
docker-compose up -d

# Verifica che sia attivo
docker-compose ps
```

### 4️⃣ Avvia l'applicazione

```bash
# Carica le variabili d'ambiente
export $(cat .env | xargs)

# Avvia Spring Boot
./mvnw spring-boot:run
```

Oppure con un solo comando:

```bash
env $(cat .env | xargs) ./mvnw spring-boot:run
```

### 5️⃣ Verifica che funzioni

```bash
# Health check
curl http://localhost:8080/actuator/health

# Risposta attesa: {"status":"UP"}
```

---

## 📮 Test con Postman

### Import Collection

1. Apri Postman
2. Importa `Gimmy-Bro API.postman_collection.json`
3. Crea un environment con le seguenti variabili:
   - `baseUrl` = `http://localhost:8080`
   - `bearerToken` = (vuoto, popolato automaticamente dopo login)
   - `refreshToken` = (vuoto, popolato automaticamente)

### Test Flow

1. **Login** → `POST /api/v1/auth/token`

   ```json
   { "email": "admin@gimmy.com", "password": "admin_password" }
   ```

2. I token vengono salvati automaticamente nelle variabili
3. Tutte le altre richieste useranno `{{bearerToken}}`

---

## 🗄️ Database

### Utenti di default (creati da Flyway)

| Email | Password | Ruolo |
|-------|----------|-------|
| `admin@gimmy.com` | `admin_password` | ADMIN |
| `pt@gimmy.com` | `pt_password` | PT |
| `trainee@gimmy.com` | `trainee_password` | TRAINEE |

### Comandi utili

```bash
# Stop database
docker-compose down

# Stop e elimina dati
docker-compose down -v

# Visualizza logs
docker-compose logs -f postgres
```

---

## 🔧 Troubleshooting

### Errore: "Connection refused"

```bash
# Verifica che PostgreSQL sia attivo
docker-compose ps

# Riavvia se necessario
docker-compose restart
```

### Errore: "JWT signature invalid"

- Assicurati che `JWT_SECRET` abbia almeno 64 caratteri
- Controlla che le variabili d'ambiente siano caricate

### Reset completo

```bash
docker-compose down -v
docker-compose up -d
./mvnw spring-boot:run
```

---

## 📁 Struttura Progetto

```
src/main/java/com/gymmybro/
├── application/           # Servizi e DTOs
├── domain/               # Entità JPA
├── infrastructure/       # Configurazioni, Security, Client esterni
├── presentation/         # REST Controllers
└── exception/            # Eccezioni personalizzate
```

---

## 🔗 Endpoints Principali

| Endpoint | Descrizione |
|----------|-------------|
| `POST /api/v1/auth/token` | Login |
| `GET /api/v1/auth/me` | Utente corrente |
| `GET /api/v1/users` | Lista utenti (Admin) |
| `GET /api/v1/exercises` | Cerca esercizi |
| `POST /api/v1/workout-plans` | Crea piano allenamento |
| `GET /api/v1/workouts/active` | Workout del giorno |

📖 **Swagger UI**: <http://localhost:8080/swagger-ui.html>
