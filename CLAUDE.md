# CLAUDE.md — SpendStat Project Guide

This file provides guidance to Claude Code for working within the SpendStat repository.

## Project Purpose

SpendStat is a personal finance tracking application. It lets a user manually enter transactions, connect a Revolut bank account via Open Banking for automatic transaction import, and view spending statistics and balance trends. It is a single-user, self-hosted application.

## Repository Layout

```
SpendStat/
├── backend/          Spring Boot 3 / Java 21 REST API
├── mobile/           React Native (Expo) cross-platform app
├── docs/             Architecture, requirements, development plan
├── docker-compose.yml  Local dev environment (PostgreSQL)
└── CLAUDE.md
```

Full module layout: see `docs/DEVELOPMENT_PLAN.md` → "Folder and Module Structure".

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.x, Spring Data JPA, Spring Security (JWT), PostgreSQL 15, Flyway migrations, Gradle (Kotlin DSL)
- **Mobile**: React Native, Expo SDK, TypeScript, Zustand, Expo Router, Victory Native (charts), Axios
- **Bank integration**: Revolut Open Banking API (OAuth 2.0 / PSD2)
- **Infra**: Docker + Docker Compose

Architectural decisions and rationale are in `docs/ARCHITECTURE.md`.

## Running Locally

```bash
# 1. Start the database
docker-compose up -d db

# 2. Run the backend (profile: local)
cd backend && ./gradlew bootRun --args='--spring.profiles.active=local'
# API available at http://localhost:8080

# 3. Run the mobile app
cd mobile && npx expo start
```

## Testing

```bash
cd backend && ./gradlew test       # unit + integration tests
cd mobile  && npx tsc --noEmit     # TypeScript type check
```

## Key Coding Conventions

### Backend — Hexagonal DDD

The backend uses Hexagonal Architecture (Ports & Adapters) with DDD. Full design in `docs/ARCHITECTURE.md → Backend Architecture`.

**Layer rules (enforced by package placement, not framework magic):**
- `domain/` — pure Java. No `@Entity`, no `@Service`, no Spring imports. Aggregates, value objects, and port interfaces live here.
- `application/` — Spring `@Service` beans that implement inbound ports and call outbound ports. No HTTP, no JPA.
- `adapter/in/web/` — Spring MVC controllers. Deserialize request → call one use-case method → serialize response. No business logic.
- `adapter/out/persistence/` — JPA entities (`@Entity` here, not in domain), Spring Data repositories, and persistence adapters that implement domain repository ports.
- `adapter/out/bank/` — HTTP clients for external bank APIs, implementing `BankProviderPort`.
- `infrastructure/security/` — Spring Security config, JWT filter.

**Naming:**
- Inbound ports: `<Action><Entity>UseCase.java` (e.g., `CreateTransactionUseCase`)
- Outbound ports: `<Entity>Repository.java` or `<Thing>Port.java`
- Persistence adapters: `<Entity>PersistenceAdapter.java`
- JPA entities: `<Entity>JpaEntity.java` (distinguishes them from domain entities)

**Other rules:**
- Domain entities enforce their own invariants (in constructors / factory methods), not in service code
- Value objects (`Money`, `CurrencyCode`, etc.) are immutable and validate on construction
- Ownership checks (user can only access their own data) go in application services
- DTO ↔ domain mapping is done in adapters (controllers map request DTOs → domain commands; persistence adapters map JPA entities ↔ domain objects)
- All schema changes via Flyway migrations in `src/main/resources/db/migration/`
- Use `UUID` primary keys everywhere
- Secrets via environment variables only — never hardcoded

### Mobile
- TypeScript strict mode is on
- State in Zustand stores under `src/store/`
- API calls in `src/api/` — never call Axios directly in components
- All money amounts stored/transmitted as integers (minor currency units) or `NUMERIC(15,4)` in the DB

## Environment Variables

Backend secrets go in `backend/src/main/resources/application-local.yml` (gitignored). Required vars: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`. See `docs/DEVELOPMENT_PLAN.md` for the full list.

## What Not to Do

- Do not add a web frontend until Phase 4 — the API is the shared backend.
- Do not use Spring Session or server-side sessions; auth is stateless JWT only.
- Do not commit `application-local.yml`, `.env`, or any file containing real secrets.
- Do not add new bank providers until the `BankProviderPort` interface is finalised in Phase 2.
- Do not put `@Entity` or any JPA annotation on domain objects in `domain/` — those belong on `*JpaEntity` classes in `adapter/out/persistence/`.
- Do not inject repositories or HTTP clients directly into controllers — they must go through a use-case (inbound port).
