# 🌊 Attendance Flow — Smart Attendance Tracking System

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/build-passing-success)](#)

**Attendance Flow** is a modern, flexible and highly efficient ecosystem for tracking and analyzing attendance, combining the power of a corporate Java backend with the convenience of an intuitive Telegram bot interface.

The project automates routine processes in educational institutions, IT courses, or companies — completely replacing outdated paper logs and cumbersome Excel spreadsheets.

---

## 📑 Table of Contents

- [Key Project Philosophy](#-key-project-philosophy)
- [Main Features](#-main-features-and-functionality)
- [Tech Stack](#-tech-stack-architecture)
- [Architecture Overview](#-architecture-overview)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [Docker Compose](#-docker-compose)
- [API Documentation](#-api-documentation)
- [Security](#-security)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Key Project Philosophy

The main idea of **Attendance Flow** is to minimize the time teachers or managers spend marking attendees, and to simplify the process for users themselves (students/employees). Thanks to Telegram integration, checking in takes only a few seconds, right inside the messenger everyone already uses.

---

## 🚀 Main Features and Functionality

### 👤 For Students / Users
- **Secure Account Linking** — a user registers in the web application and receives a one-time token, which is then sent to the Telegram bot (`/start`) to link the Telegram account to the system account. No passwords are ever handled by the bot.
- **Schedule Lookup** — browse the class schedule for any of your groups, filtered by day of the week, directly from an inline keyboard.
- **Attendance Statistics** — check your personal attendance stats per group on demand.
- **Notification Preferences** — enable or disable push notifications from the bot with a single tap.
- **Multi-language Interface** — the bot UI is fully localized (English, Ukrainian, Polish) via Spring's `MessageSource`, with the language switchable at any time from the settings menu.
- **Self-service Logout** — unlink the Telegram account from the system at any time, with an explicit confirmation step.

### 👔 For Teachers / Moderators / Administrators
- **Automated Session Generation** — create unique events (lectures, practicals, meetings) with an open attendance window.
- **Flexible Log Management** — manually adjust statuses (present, late, valid reason, absent) when needed.
- **Deep Analytics & Reports** — automatic statistics by group, course, or individual user.
- **Data Export** — download attendance reports in convenient formats (e.g. Excel) for reporting.

### 🤖 Telegram Bot Layer
- Implemented as a **long-polling** bot (`TelegramLongPollingBot`) — no public webhook/HTTPS endpoint required to run it.
- Handles both plain text messages (`onUpdateReceived` → `handleTextMessage`) and inline-keyboard callback queries (`handleCallBackQuery`), each routed by a set of callback-data prefixes (group/day/stat/notification/language/logout).
- Talks to the backend through the same `MessengerAccountService` used by the rest of the application (single source of truth, no duplicated business logic).
- A dedicated `TelegramNotificationService` centralizes outgoing/localized messages and keyboard rendering, keeping the controller focused on routing.
- Account state is tracked with a `BotState` enum (e.g. `AWAITING_TOKEN`) so the bot knows whether it's mid-linking or fully authenticated for a given chat.

---

## 🛠 Tech Stack & Architecture

| Layer | Technology | Purpose |
| :--- | :--- | :--- |
| **Language / Runtime** | Java 21 (LTS) | Core language, modern language features, JVM performance |
| **Framework** | Spring Boot 4.0.1 | Application bootstrap, auto-configuration, DI/IoC |
| **Web** | Spring Web MVC | REST controllers, request routing |
| **View Layer** | Thymeleaf | Server-side rendering for the web UI |
| **Security** | Spring Security | Authentication & authorization |
| **Security Integration** | Thymeleaf Extras Spring Security 6 | Security-aware rendering in templates |
| **OAuth2** | Spring Boot OAuth2 Client | Third-party / social login integration |
| **Persistence** | Spring Data JPA + Hibernate | ORM, repository abstraction |
| **Production Database** | PostgreSQL | Reliable relational storage for production |
| **Local/Test Database** | H2 (file-based) | Lightweight local development & testing |
| **Caching / Session Store** | Redis | Caching layer (TTL-based) |
| **Monitoring** | Prometheus + Grafana | Metrics scraping & dashboards (via `docker-compose.yml`) |
| **Migrations** | Liquibase | Version-controlled, reproducible schema changes |
| **Validation** | Jakarta Bean Validation (Spring Boot Starter Validation) | Declarative input validation |
| **Messaging Bot** | TelegramBots Spring Boot Starter (6.9.7.1) | Long-polling Telegram bot integration |
| **Internationalization** | Spring `MessageSource` | Bot & UI messages localized in English, Ukrainian, and Polish |
| **Email** | Spring Boot Starter Mail | Transactional/notification emails |
| **Reporting** | Apache POI (poi-ooxml 5.2.5) | Excel (.xlsx) report generation/export |
| **API Docs** | springdoc-openapi (2.8.5) + Swagger UI | Interactive, auto-generated API documentation |
| **Boilerplate Reduction** | Lombok | Getters/setters/builders via annotations |
| **Build Tool** | Maven (with Maven Wrapper) | Dependency management & build automation |
| **Testing** | Spring Boot Test starters (Data JPA, Security, Thymeleaf, Validation, Web MVC) | Layer-focused integration testing |

---

## 🏗 Architecture Overview

```
                         ┌─────────────────────┐
                         │   Telegram Client    │
                         └──────────┬───────────┘
                                    │
                          (Bot API / long-polling)
                                    │
┌───────────────────────────────────────────────────────────────┐
│                      Attendance Flow Backend                    │
│                   (docker service: app-backend)                 │
│   ┌────────────┐     ┌────────────────┐     ┌────────────────┐ │
│   │  Thymeleaf │     │  REST/MVC       │     │  Telegram Bot   │ │
│   │  Web UI    │◄───►│  Controllers    │◄───►│  Handlers       │ │
│   └────────────┘     └────────┬───────┘     └────────────────┘ │
│                                │                                  │
│                     ┌──────────▼──────────┐                     │
│                     │   Service Layer      │                     │
│                     │ (business logic,     │                     │
│                     │  security, mailing,  │                     │
│                     │  report generation)  │                     │
│                     └──────────┬──────────┘                     │
│                                │                                  │
│                     ┌──────────▼──────────┐                     │
│                     │  Repository Layer     │                    │
│                     │  (Spring Data JPA)     │                   │
│                     └──────────┬──────────┘                     │
└──────────────┬─────────────────┼──────────────┬──────────────────┘
               │                 │                │
      ┌────────▼────────┐ ┌──────▼──────┐  (metrics scrape,
      │  postgres-db      │ │ redis-cache  │   if Actuator +
      │  (docker service)  │ │ (docker svc)  │   Micrometer added)
      └─────────────────────┘ └──────────────┘        │
                                                ┌───────▼────────┐
                                                │  prometheus      │
                                                │  (docker service)│
                                                └───────┬────────┘
                                                         │
                                                ┌───────▼────────┐
                                                │  grafana         │
                                                │  (docker service)│
                                                └──────────────────┘

        H2 (file-based, ./data/attendance_db) replaces postgres-db
        when running with the developer profile outside Docker.
        Postgres/H2 schema is Liquibase-managed.
```

Authentication is handled with **stateless JWT tokens stored in HttpOnly cookies**, verified by a Spring Security filter chain shared by both the web controllers and (where applicable) the bot-linked API endpoints. OAuth2 login is available as an additional entry point alongside the custom `AuthController`.

---

## 📋 Prerequisites

Before you begin, make sure you have installed:

- **Git**
- **Java JDK 21**
- **Docker** (recommended, for running PostgreSQL locally)
- **PostgreSQL 17** (if not using Docker)
- **A Telegram bot token** — create one via [@BotFather](https://telegram.me/BotFather)

> No local Maven installation is required — the project ships with the Maven Wrapper (`mvnw` / `mvnw.cmd`).

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/RostyslavBehei/attendance-flow.git
cd attendance-flow
```

### 2. Start a PostgreSQL instance (optional — H2 works out of the box)

```bash
docker run --name attendance-flow-db \
  -e POSTGRES_DB=attendance \
  -e POSTGRES_USER=attendance_user \
  -e POSTGRES_PASSWORD=change_me \
  -p 5432:5432 \
  -d postgres:17
```

If you skip this step, the application can run against the bundled file-based **H2** database for quick local development.

### 3. Configure environment variables

See [Configuration](#-configuration) below.

### 4. Build the project

```bash
./mvnw clean install
```

### 5. Run the application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080` by default, and Liquibase will automatically apply schema migrations on startup.

---

## ⚙️ Configuration

The application reads its configuration from `application.yml` (or `application.properties`), which in turn should reference **environment variables** — nothing here should ever be hardcoded or committed with real values.

### 1. Create your `.env` file

Copy the example file and fill in real values:

```bash
cp .env.example .env
```

`.env.example` (commit this to the repo root — placeholders only, never real secrets):

```dotenv
# ── App Setting ──────────────────────────────────────────
APP_BASE_URL=http://localhost:8080
SERVER_PORT=8080

# ── Database Setting (H2 file-based, Developer Profile) ──
DB_H2_URL=jdbc:h2:file:./data/attendance_db
DB_H2_DRIVER=org.h2.Driver
DB_H2_USERNAME=sa
DB_H2_PASSWORD=change_me

# ── Database Setting (PostgreSQL, Production Profile) ────
DB_POSTGRES_URL=jdbc:postgresql://postgres-db:5432/attendance_flow
DB_POSTGRES_DRIVER=org.postgresql.Driver
DB_POSTGRES_USERNAME=attendance_user
DB_POSTGRES_PASSWORD=change_me

# ── Telegram Bot Setting (see TelegramBotController) ─────
TG_BOT_NAME=your_bot_username
TG_BOT_TOKEN=your-botfather-token

# ── Mail Server Setting ───────────────────────────────────
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your-app-password

# ── Redis Setting ─────────────────────────────────────────
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
SPRING_REDIS_TTL=60

# ── OAuth2 Setting ─────────────────────────────────────────
SPRING_OAUTH2_CLIENT_ID=your-oauth-client-id
SPRING_OAUTH2_CLIENT_SECRET=your-oauth-client-secret

# ── JWT Setting ───────────────────────────────────────────
JWT_SECRET=replace-with-a-long-random-secret
JWT_EXPIRATION=86400000
```


### 2. Reference the variables in `application.yml`

Spring Boot does **not** load `.env` files automatically — it reads configuration from `application.yml`, system properties, and OS environment variables.

There are two different situations here, and they're easy to mix up:

- **Running via `docker-compose.yml` (prod profile):** `docker-compose.yml` already translates every `.env` name into the exact environment variable Spring expects (`DB_POSTGRES_URL` → `SPRING_DATASOURCE_URL`, `TG_BOT_TOKEN` → `TELEGRAM_BOT_TOKEN`, etc.). Spring Boot's **relaxed binding** automatically converts a `SCREAMING_SNAKE_CASE` environment variable into the matching `dot.case` property (`SPRING_DATASOURCE_URL` → `spring.datasource.url`, `TELEGRAM_BOT_TOKEN` → `telegram.bot.token`, `JWT_SECRET` → `jwt.secret`). **No explicit `${VAR}` placeholder is required in `application.yml`** for this path — the container environment alone is enough.
- **Running locally without Docker (dev profile, H2):** `.env` names like `DB_H2_URL`, `TG_BOT_TOKEN`, `TG_BOT_NAME` don't match any Spring-recognized property name, so they **do** need to be mapped explicitly with `${VAR_NAME}` placeholders in `application-dev.yml` (or an equivalent profile-specific file), for example:

```yaml
# application-dev.yml
server:
  port: ${SERVER_PORT}

app:
  base-url: ${APP_BASE_URL}

telegram:
  bot:
    token: ${TG_BOT_TOKEN}
    username: ${TG_BOT_NAME}

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION}

spring:
  datasource:
    url: ${DB_H2_URL}
    driver-class-name: ${DB_H2_DRIVER}
    username: ${DB_H2_USERNAME}
    password: ${DB_H2_PASSWORD}
  mail:
    host: ${MAIL_HOST}
    port: ${MAIL_PORT}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
  data:
    redis:
      host: ${SPRING_REDIS_HOST}
      port: ${SPRING_REDIS_PORT}
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${SPRING_OAUTH2_CLIENT_ID}
            client-secret: ${SPRING_OAUTH2_CLIENT_SECRET}
```

### 3. Load `.env` when running the app

Pick whichever fits your workflow:

- **Shell (Linux/macOS)** — export the file into your session before running:
  ```bash
  export $(grep -v '^#' .env | xargs)
  ./mvnw spring-boot:run
  ```
- **IDE (IntelliJ IDEA)** — install the "EnvFile" plugin and attach `.env` to your Run/Debug configuration, or add each variable manually under *Environment variables*.
- **Docker Compose** — reference it directly, no manual export needed (see [Docker Compose](#-docker-compose) below).
- **Automatic `.env` loading without Docker** — if you'd rather not export variables manually every time, add the [`me.paulschwarz:spring-dotenv`](https://github.com/paulschwarz/spring-dotenv) dependency to `pom.xml`; it loads `.env` into the Spring `Environment` automatically at startup.

### Variable reference

| Variable | Description | Example |
| :--- | :--- | :--- |
| `APP_BASE_URL` | Public base URL of the app (used in emails, OAuth2 redirects, etc.) | `http://localhost:8080` |
| `SERVER_PORT` | Port the embedded server listens on | `8080` |
| `DB_H2_URL` | H2 file-based JDBC URL (dev profile) | `jdbc:h2:file:./data/attendance_db` |
| `DB_H2_DRIVER` | H2 JDBC driver class | `org.h2.Driver` |
| `DB_H2_USERNAME` / `DB_H2_PASSWORD` | H2 credentials (dev profile) | `sa` / `change_me` |
| `DB_POSTGRES_URL` | PostgreSQL JDBC URL (prod profile) — host matches the `postgres-db` Compose service name | `jdbc:postgresql://postgres-db:5432/attendance_flow` |
| `DB_POSTGRES_DRIVER` | PostgreSQL JDBC driver class | `org.postgresql.Driver` |
| `DB_POSTGRES_USERNAME` / `DB_POSTGRES_PASSWORD` | PostgreSQL credentials (prod profile) | `attendance_user` / `change_me` |
| `TG_BOT_NAME` | Telegram bot username; read via `@Value("${telegram.bot.username}")` | `attendance_flow_bot` |
| `TG_BOT_TOKEN` | Telegram bot token from @BotFather; read via `@Value("${telegram.bot.token}")` | `123456:ABC-DEF...` |
| `MAIL_HOST` / `MAIL_PORT` | SMTP server & port | `smtp.gmail.com` / `587` |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | SMTP credentials (use an App Password, not your main account password) | — |
| `SPRING_REDIS_HOST` / `SPRING_REDIS_PORT` | Redis connection | `localhost` / `6379` |
| `SPRING_REDIS_TTL` | Default cache entry TTL, in seconds | `60` |
| `SPRING_OAUTH2_CLIENT_ID` / `SPRING_OAUTH2_CLIENT_SECRET` | Google OAuth2 client credentials | — |
| `JWT_SECRET` | Secret key used to sign JWT tokens | a long random string |
| `JWT_EXPIRATION` | Access token lifetime, in milliseconds | `86400000` (24h) |

> ⚠️ Never commit real secrets to version control. Only `.env.example` with placeholders belongs in git; your actual `.env` stays local and git-ignored.

---

## ▶️ Running the Application

```bash
# Development mode (H2, hot reload friendly)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production-style run (PostgreSQL)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Or run the packaged jar directly:

```bash
./mvnw clean package
java -jar target/attendance-flow-0.0.1-SNAPSHOT.jar
```

---

## 🐳 Docker Compose

The repo's `docker-compose.yml` spins up the full stack: the Spring Boot app, PostgreSQL, Redis, and a Prometheus + Grafana monitoring pair.

| Service | Container name | Port(s) | Purpose |
| :--- | :--- | :--- | :--- |
| `app-backend` | `attendance_flow_app` | `${SERVER_PORT:-8080}` | The Spring Boot application |
| `postgres-db` | `attendance_flow_db` | `5432` | Production database |
| `redis-cache` | `attendance_flow_redis` | `6379` | Caching layer |
| `prometheus` | `attendance_flow_prometheus` | `9090` | Metrics scraping |
| `grafana` | `attendance_flow_grafana` | `3000` | Metrics dashboards |

```yaml
version: '3.8'

services:
  app-backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: attendance_flow_app
    ports:
      - "${SERVER_PORT:-8080}:${SERVER_PORT:-8080}"
    environment:
      # App
      APP_BASE_URL: ${APP_BASE_URL}
      SERVER_PORT: ${SERVER_PORT}

      # Database
      SPRING_DATASOURCE_URL: ${DB_POSTGRES_URL}
      SPRING_DATASOURCE_USERNAME: ${DB_POSTGRES_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${DB_POSTGRES_PASSWORD}
      SPRING_DATASOURCE_DRIVER_CLASS_NAME: ${DB_POSTGRES_DRIVER}

      # Mail
      SPRING_MAIL_HOST: ${MAIL_HOST}
      SPRING_MAIL_PORT: ${MAIL_PORT}
      SPRING_MAIL_USERNAME: ${MAIL_USERNAME}
      SPRING_MAIL_PASSWORD: ${MAIL_PASSWORD}

      # Telegram Bot
      TELEGRAM_BOT_USERNAME: ${TG_BOT_NAME}
      TELEGRAM_BOT_TOKEN: ${TG_BOT_TOKEN}

      # Redis
      SPRING_DATA_REDIS_HOST: redis-cache
      SPRING_DATA_REDIS_PORT: ${SPRING_REDIS_PORT}
      SPRING_REDIS_TTL: ${SPRING_REDIS_TTL}

      # OAuth2
      SPRING_OAUTH2_CLIENT_ID: ${SPRING_OAUTH2_CLIENT_ID}
      SPRING_OAUTH2_CLIENT_SECRET: ${SPRING_OAUTH2_CLIENT_SECRET}

      # JWT
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION}

    depends_on:
      postgres-db:
        condition: service_healthy
      redis-cache:
        condition: service_started
    networks:
      - attendance_net

  postgres-db:
    image: postgres:17-alpine
    container_name: attendance_flow_db
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: attendance_flow
      POSTGRES_USER: ${DB_POSTGRES_USERNAME}
      POSTGRES_PASSWORD: ${DB_POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - attendance_net
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_POSTGRES_USERNAME} -d attendance_flow"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis-cache:
    image: redis:7-alpine
    container_name: attendance_flow_redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - attendance_net

  prometheus:
    image: prom/prometheus:latest
    container_name: attendance_flow_prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    networks:
      - attendance_net

  grafana:
    image: grafana/grafana:latest
    container_name: attendance_flow_grafana
    ports:
      - "3000:3000"
    depends_on:
      - prometheus
    volumes:
      - grafana_data:/var/lib/grafana
    networks:
      - attendance_net

volumes:
  postgres_data:
  redis_data:
  prometheus_data:
  grafana_data:

networks:
  attendance_net:
    driver: bridge
```

### Important behavior notes

- **Redis host is hardcoded, not read from `.env`.** `SPRING_DATA_REDIS_HOST` is set to the literal `redis-cache` (the Compose service name) — the `SPRING_REDIS_HOST` variable in `.env` is only relevant when running the app **outside** Docker (e.g. `./mvnw spring-boot:run` against a locally installed Redis on `localhost`). Inside Compose, that `.env` value is effectively ignored.
- **`.env` variable names are translated into Spring-recognized names at the Compose level.** `.env` uses project-specific names (`DB_POSTGRES_URL`, `MAIL_HOST`, `TG_BOT_TOKEN`, …); `docker-compose.yml` maps each of them onto the environment variable name Spring Boot actually expects (`SPRING_DATASOURCE_URL`, `SPRING_MAIL_HOST`, `TELEGRAM_BOT_TOKEN`, …). Because Spring Boot's relaxed binding automatically converts `SCREAMING_SNAKE_CASE` environment variables into the matching `dot.case` property (`SPRING_DATASOURCE_URL` → `spring.datasource.url`, `TELEGRAM_BOT_TOKEN` → `telegram.bot.token`, `JWT_SECRET` → `jwt.secret`, etc.), **no explicit `${VAR}` placeholders are required in `application.yml` for the Docker/prod run** — the container environment alone is enough. Explicit `${VAR}` placeholders in `application.yml` (like in the earlier example in this README) only become necessary for the **local, non-Docker dev profile**, where `.env` names like `DB_H2_URL` or `TG_BOT_TOKEN` don't match any Spring-recognized property name and must be mapped manually.
- **`docker-compose.yml` requires a `prometheus.yml` file at the repo root** (mounted into the `prometheus` container as `/etc/prometheus/prometheus.yml`). Without it, `docker compose up` will fail to start the `prometheus` service. A minimal example:
  ```yaml
  global:
    scrape_interval: 15s
    evaluation_interval: 15s

  scrape_configs:
    - job_name: 'attendance-flow-app'
      metrics_path: '/actuator/prometheus'
      static_configs:
        - targets: ['app-backend:8080']

    - job_name: 'prometheus'
      static_configs:
        - targets: ['localhost:9090']
  ```

### Starting the stack

```bash
docker compose up -d --build
```

Follow app logs:

```bash
docker compose logs -f app-backend
```

Access points once everything is healthy:
- App: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (default login `admin` / `admin`, change on first login)

Stop and remove containers (keeps named volumes — data persists):

```bash
docker compose down
```

Stop and wipe all data, including the database and Grafana dashboards:

```bash
docker compose down -v
```

---

## 📝 API Documentation

Once the application is running, interactive API documentation is available via **springdoc-openapi** and **Swagger UI**:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

This is the primary reference for frontend and Telegram bot integrators to explore endpoints, request/response schemas, and try calls directly from the browser.

---

## 🔐 Security

- **Stateless authentication** using **JWT** tokens stored in **HttpOnly cookies** (not accessible to client-side JavaScript, mitigating XSS token theft).
- **Spring Security filter chain** validates the token on every request and populates the security context.
- **OAuth2 Client** support for third-party login providers alongside the custom `AuthController` for username/password flows.
- **Jakarta Bean Validation** enforces input correctness at the DTO layer before it reaches business logic.
- Passwords are never stored or transmitted in plain text.

---

## 🧪 Testing

The project includes Spring Boot test starters for each major layer:

- `spring-boot-starter-data-jpa-test` — repository/persistence layer tests
- `spring-boot-starter-security-test` — authentication & authorization tests
- `spring-boot-starter-thymeleaf-test` — view rendering tests
- `spring-boot-starter-validation-test` — DTO validation tests
- `spring-boot-starter-webmvc-test` — controller/API tests

Run the full test suite with:

```bash
./mvnw test
```

---

## 📂 Project Structure

```
attendance-flow/
├── .mvn/wrapper/                       # Maven Wrapper files
├── src/
│   ├── main/
│   │   ├── java/com/attendance/flow/
│   │   │   ├── bot/
│   │   │   │   ├── TelegramBotController.java   # Long-polling bot: text & callback-query routing
│   │   │   │   └── notification/
│   │   │   │       └── TelegramNotificationService.java  # Localized outgoing messages & keyboards
│   │   │   ├── config/                 # Spring Security, OAuth2, Swagger, JWT configuration
│   │   │   ├── controller/             # REST & MVC controllers (incl. AuthController)
│   │   │   ├── service/                # Business logic (incl. MessengerAccountService)
│   │   │   ├── repository/             # Spring Data JPA repositories
│   │   │   ├── model/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── appGroup/       # AppGroupSummaryResponse, ...
│   │   │   │   │   ├── attendanceRecord/  # StudentAttendanceStatsResponse, ...
│   │   │   │   │   ├── lesson/         # LessonResponse, ...
│   │   │   │   │   ├── messengerAccount/  # MessengerAccountResponse, ...
│   │   │   │   │   └── schedule/       # ScheduleResponse, ...
│   │   │   │   └── enums/              # BotState, Language, ...
│   │   │   └── AttendanceFlowApplication.java
│   │   └── resources/
│   │       ├── db/changelog/           # Liquibase changelogs
│   │       ├── templates/              # Thymeleaf views
│   │       ├── messages*.properties    # i18n bundles (EN / UK / PL) used by MessageSource
│   │       └── application.yml
│   └── test/                           # Unit & integration tests
├── .env.example                        # Template for local environment variables (see Configuration)
├── mvnw / mvnw.cmd                     # Maven Wrapper scripts
├── pom.xml                             # Project dependencies & build configuration
├── LICENSE
└── README.md
```
---

## 🗺 Roadmap

- [ ] Geolocation-based check-in validation
- [ ] Push notifications via Telegram for upcoming sessions
- [ ] Role-based dashboard for administrators
- [ ] Attendance analytics with charts (weekly/monthly trends)
- [ ] Dockerized full-stack deployment (`docker-compose`)
- [ ] CI/CD pipeline (GitHub Actions)

---

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add your feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

Please make sure new code is covered by tests and follows the existing project conventions.

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 📬 Contact

Maintained by [Rostyslav Behei](https://github.com/RostyslavBehei).
For questions, bug reports, or feature requests, please [open an issue](https://github.com/RostyslavBehei/attendance-flow/issues).