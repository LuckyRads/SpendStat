# SpendStat — Architecture

## Table of Contents
1. [System Overview](#system-overview)
2. [Tech Stack](#tech-stack)
3. [Backend Architecture — Hexagonal DDD](#backend-architecture--hexagonal-ddd)
4. [System Components](#system-components)
5. [API Design](#api-design)
6. [Database Schema](#database-schema)
7. [Bank Integration Strategy](#bank-integration-strategy)
8. [Authentication](#authentication)
9. [Deployment Strategy](#deployment-strategy)

---

## System Overview

SpendStat is a personal finance tracking application that allows a user to:
- Connect bank accounts via Open Banking APIs (Revolut first, PSD2-compatible banks later)
- Import transactions automatically from connected banks
- Enter transactions manually
- View spending statistics, category breakdowns, and balance trends over time

The system is a **single-user, self-hosted** application split into three primary layers:
- **Backend API** — Java / Spring Boot: business logic, bank integrations, data persistence
- **Mobile Client** — React Native (Expo): cross-platform Android and iOS UI
- **External Services** — Revolut Open Banking API (and future PSD2-compatible banks)

---

## Tech Stack

| Layer | Technology | Justification |
|---|---|---|
| Backend language | Java 21 (LTS) | Developer's primary language; mature ecosystem |
| Backend framework | Spring Boot 3.x | Convention-over-configuration; excellent REST, security, and data libraries |
| Database ORM | Spring Data JPA + Hibernate | Standard Spring persistence; works naturally with relational data |
| Database | PostgreSQL 15+ | Robust relational DB; excellent JSON support for caching bank API responses |
| Migrations | Flyway | Version-controlled schema changes; integrates cleanly with Spring Boot |
| Build tool | Gradle (Kotlin DSL) | Modern, expressive build; better dependency management than Maven |
| Mobile framework | React Native (Expo SDK) | Developer has React experience; Expo reduces native toolchain friction |
| Mobile language | TypeScript | Type safety, better DX, catches errors at compile time |
| Mobile state | Zustand | Lightweight, minimal boilerplate; fits a single-user app scope |
| Mobile navigation | Expo Router (file-based) | Built into Expo; Next.js-like routing mental model |
| Mobile charts | Victory Native | Hardware-accelerated charting for analytics views |
| Auth tokens | JWT (access + refresh) | Stateless; fits mobile-first design; Spring Security first-class support |
| Bank integration | Revolut Open Banking API (OAuth 2.0) | Direct Revolut access; PSD2-compatible spec enables future bank additions |
| Containerisation | Docker + Docker Compose | Reproducible local dev and simple self-hosted deployment |
| CI | GitHub Actions | Free for open-source; straightforward Java + Node pipelines |

---

## Backend Architecture — Hexagonal DDD

The backend follows **Hexagonal Architecture** (Ports & Adapters) organized around **Domain-Driven Design** concepts. The core rule is the **Dependency Rule**: source code dependencies point only inward — infrastructure knows about the application, the application knows about the domain, the domain knows nothing outside itself.

### Layer Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Adapter — Inbound                     │
│  REST Controllers  │  Schedulers  │  (future: CLI/gRPC) │
└──────────────────────────┬──────────────────────────────┘
                           │  calls inbound ports
┌──────────────────────────▼──────────────────────────────┐
│                     Application Layer                    │
│   Use Case Services  (implement inbound ports,          │
│                        call outbound ports)              │
└──────────────────────────┬──────────────────────────────┘
                           │  calls outbound ports
┌──────────────────────────▼──────────────────────────────┐
│                      Domain Layer                        │
│  Aggregates · Value Objects · Domain Services           │
│  Inbound Ports (use case interfaces)                    │
│  Outbound Ports (repository + provider interfaces)      │
│                  (zero framework dependencies)           │
└──────────────────────────┬──────────────────────────────┘
                           │  implemented by
┌──────────────────────────▼──────────────────────────────┐
│                    Adapter — Outbound                    │
│  JPA Persistence Adapters  │  Bank HTTP Adapters         │
└─────────────────────────────────────────────────────────┘
```

### Packages

```
com.spendstat/
├── domain/                          ← pure Java, zero Spring/JPA imports
│   ├── shared/
│   │   ├── Money.java               (value object: amount + currency)
│   │   ├── CurrencyCode.java        (value object)
│   │   └── UserId.java              (value object)
│   ├── transaction/
│   │   ├── Transaction.java         (aggregate root)
│   │   ├── TransactionId.java       (value object)
│   │   ├── TransactionType.java     (enum: INCOME, EXPENSE, TRANSFER)
│   │   └── port/
│   │       ├── in/
│   │       │   ├── CreateTransactionUseCase.java
│   │       │   ├── UpdateTransactionUseCase.java
│   │       │   ├── DeleteTransactionUseCase.java
│   │       │   └── QueryTransactionsUseCase.java
│   │       └── out/
│   │           └── TransactionRepository.java
│   ├── account/
│   │   ├── Account.java
│   │   ├── AccountId.java
│   │   └── port/
│   │       ├── in/
│   │       │   ├── CreateAccountUseCase.java
│   │       │   ├── UpdateAccountUseCase.java
│   │       │   └── QueryAccountsUseCase.java
│   │       └── out/
│   │           └── AccountRepository.java
│   ├── category/
│   │   ├── Category.java
│   │   ├── CategoryId.java
│   │   └── port/
│   │       ├── in/
│   │       │   └── ManageCategoriesUseCase.java
│   │       └── out/
│   │           └── CategoryRepository.java
│   ├── statistics/
│   │   └── port/
│   │       ├── in/
│   │       │   └── GetStatisticsUseCase.java
│   │       └── out/
│   │           └── StatisticsQueryPort.java
│   └── bank/
│       ├── BankConnection.java
│       ├── BankConnectionId.java
│       └── port/
│           ├── in/
│           │   ├── ConnectBankUseCase.java
│           │   └── SyncBankTransactionsUseCase.java
│           └── out/
│               ├── BankConnectionRepository.java
│               └── BankProviderPort.java           ← replaces old BankProvider interface
│
├── application/                     ← orchestrates use cases; depends on domain only
│   ├── transaction/
│   │   └── TransactionService.java  (implements all Transaction inbound ports)
│   ├── account/
│   │   └── AccountService.java
│   ├── category/
│   │   └── CategoryService.java
│   ├── statistics/
│   │   └── StatisticsService.java
│   └── bank/
│       └── BankSyncService.java
│
└── adapter/
    ├── in/                          ← driving adapters
    │   ├── web/
    │   │   ├── TransactionController.java
    │   │   ├── AccountController.java
    │   │   ├── CategoryController.java
    │   │   ├── StatisticsController.java
    │   │   ├── AuthController.java
    │   │   ├── BankConnectionController.java
    │   │   └── dto/                 (request/response DTOs, @Valid annotations here)
    │   │       ├── CreateTransactionRequest.java
    │   │       ├── TransactionResponse.java
    │   │       └── ...
    │   └── scheduler/
    │       └── BankSyncScheduler.java  (@Scheduled lives here, calls use case)
    └── out/                         ← driven adapters
        ├── persistence/             (JPA entities and Spring Data repos — framework deps live here)
        │   ├── transaction/
        │   │   ├── TransactionPersistenceAdapter.java  (implements TransactionRepository port)
        │   │   ├── TransactionJpaRepository.java       (Spring Data interface)
        │   │   └── TransactionJpaEntity.java           (@Entity lives here, not in domain)
        │   ├── account/
        │   │   ├── AccountPersistenceAdapter.java
        │   │   ├── AccountJpaRepository.java
        │   │   └── AccountJpaEntity.java
        │   ├── category/
        │   │   ├── CategoryPersistenceAdapter.java
        │   │   ├── CategoryJpaRepository.java
        │   │   └── CategoryJpaEntity.java
        │   └── user/
        │       ├── UserPersistenceAdapter.java
        │       ├── UserJpaRepository.java
        │       └── UserJpaEntity.java
        └── bank/
            └── revolut/
                ├── RevolutBankAdapter.java   (implements BankProviderPort)
                └── RevolutApiClient.java     (HTTP client using RestClient/WebClient)
```

### Key Design Rules

1. **Domain entities carry no framework annotations.** `@Entity`, `@Column`, Spring beans — none of these appear in `domain/`. JPA entities live in `adapter/out/persistence/` and are mapped to/from domain objects in the persistence adapter.

2. **Ports are interfaces owned by the domain.** Inbound ports (`*UseCase.java`) are called by controllers. Outbound ports (`*Repository.java`, `BankProviderPort.java`) are called by application services and implemented by adapters.

3. **Application services are the only place use-case logic lives.** They implement inbound ports, call outbound ports, and may call domain service methods. They do not call controllers or adapters directly.

4. **Controllers are thin.** They deserialize the request, call exactly one use-case method, serialize the response. No business logic.

5. **Value objects enforce invariants.** `Money` cannot have a negative amount. `CurrencyCode` validates the ISO-4217 code. Validation happens in the constructor, not in service code.

---

## System Components

```
┌─────────────────────────────────────────────────────────────────┐
│                        Mobile App (Expo/RN)                     │
│   ┌──────────┐  ┌──────────────┐  ┌─────────────────────────┐  │
│   │ Auth     │  │ Transactions │  │ Analytics / Charts      │  │
│   │ Screens  │  │ (manual/sync)│  │ (Zustand + Victory)     │  │
│   └──────────┘  └──────────────┘  └─────────────────────────┘  │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTPS / REST + JWT
┌───────────────────────────▼─────────────────────────────────────┐
│                      Spring Boot API                            │
│  ┌────────────┐  ┌───────────────┐  ┌──────────────────────┐   │
│  │ Auth       │  │  Transaction  │  │  Bank Integration    │   │
│  │ Controller │  │  Controller   │  │  Service             │   │
│  └────────────┘  └───────────────┘  └──────────┬───────────┘   │
│  ┌──────────────────────────────────────────────┼───────────┐   │
│  │            Spring Data JPA / Hibernate       │           │   │
│  └──────────────────────────────────────────────┼───────────┘   │
└──────────────────────────────────────────────────┼──────────────┘
                                                   │ OAuth 2.0
┌──────────────────────────────────────────────────▼──────────────┐
│               PostgreSQL 15                                      │
└──────────────────────────────────────────────────────────────────┘
                                                   │
┌──────────────────────────────────────────────────▼──────────────┐
│               Revolut Open Banking API                           │
│               (future: other PSD2 banks)                         │
└──────────────────────────────────────────────────────────────────┘
```

---

## API Design

All endpoints are REST, JSON, versioned under `/api/v1/`.

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/auth/login` | Authenticate user, return JWT pair |
| `POST` | `/api/v1/auth/refresh` | Refresh access token |
| `GET` | `/api/v1/transactions` | List transactions (paginated, filterable) |
| `POST` | `/api/v1/transactions` | Create manual transaction |
| `PUT` | `/api/v1/transactions/{id}` | Update transaction |
| `DELETE` | `/api/v1/transactions/{id}` | Delete transaction |
| `GET` | `/api/v1/categories` | List categories |
| `POST` | `/api/v1/categories` | Create category |
| `GET` | `/api/v1/accounts` | List connected bank accounts |
| `POST` | `/api/v1/accounts/connect` | Initiate bank account OAuth flow |
| `POST` | `/api/v1/accounts/{id}/sync` | Trigger manual transaction sync |
| `GET` | `/api/v1/stats/summary` | Balance summary for a period |
| `GET` | `/api/v1/stats/by-category` | Spending breakdown by category |
| `GET` | `/api/v1/stats/trends` | Spending/income trends over time |

**Pagination** uses cursor-based pagination (`?cursor=&limit=`) for transaction lists.

**Filtering** supports `?from=ISO_DATE&to=ISO_DATE&category=&account=` query params.

---

## Database Schema

```sql
-- Core tables (simplified)

users (
  id          UUID PRIMARY KEY,
  email       TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  created_at  TIMESTAMPTZ DEFAULT now()
)

bank_accounts (
  id              UUID PRIMARY KEY,
  user_id         UUID REFERENCES users(id),
  provider        TEXT NOT NULL,           -- 'revolut', 'monzo', etc.
  external_id     TEXT NOT NULL,           -- bank-side account ID
  display_name    TEXT,
  currency        CHAR(3) NOT NULL,
  access_token    TEXT,                    -- encrypted
  refresh_token   TEXT,                    -- encrypted
  token_expires_at TIMESTAMPTZ,
  last_synced_at  TIMESTAMPTZ,
  created_at      TIMESTAMPTZ DEFAULT now()
)

categories (
  id          UUID PRIMARY KEY,
  user_id     UUID REFERENCES users(id),
  name        TEXT NOT NULL,
  color       TEXT,
  icon        TEXT,
  is_default  BOOLEAN DEFAULT FALSE
)

transactions (
  id              UUID PRIMARY KEY,
  user_id         UUID REFERENCES users(id),
  account_id      UUID REFERENCES bank_accounts(id) NULL,  -- null = manual
  category_id     UUID REFERENCES categories(id),
  external_id     TEXT,                    -- bank-side tx ID for dedup
  amount          NUMERIC(15,4) NOT NULL,  -- positive = income, negative = expense
  currency        CHAR(3) NOT NULL,
  description     TEXT,
  merchant        TEXT,
  tx_date         DATE NOT NULL,
  source          TEXT NOT NULL,           -- 'manual' | 'revolut' | ...
  raw_data        JSONB,                   -- original bank API response
  created_at      TIMESTAMPTZ DEFAULT now(),
  UNIQUE (account_id, external_id)
)
```

Sensitive fields (`access_token`, `refresh_token`) are encrypted at-rest using AES-256 before storage (via a Spring `@Converter`).

---

## Bank Integration Strategy

### Revolut Open Banking (Phase 2)

Revolut exposes a PSD2-compliant Open Banking API.

**Flow:**
1. User taps "Connect Revolut" in the mobile app.
2. Backend generates an authorisation URL and returns it to the app.
3. App opens a WebView / system browser to the Revolut consent screen.
4. On redirect, the backend exchanges the authorisation code for access + refresh tokens (stored encrypted in `bank_accounts`).
5. A scheduled job (Spring `@Scheduled`) polls for new transactions daily (or on manual trigger).
6. New transactions are deduplicated by `external_id` before insertion.

**Future banks:** Any PSD2-compliant bank can be added by implementing a `BankProvider` interface that wraps the OAuth flow and transaction fetch. The rest of the system is bank-agnostic.

### BankProviderPort (outbound port, sketch)

Defined in `domain/bank/port/out/BankProviderPort.java`. Implemented by `RevolutBankAdapter` in `adapter/out/bank/revolut/`.

```java
// domain/bank/port/out/BankProviderPort.java
public interface BankProviderPort {
    String getProviderName();
    String buildAuthorizationUrl(String state);
    BankTokens exchangeCode(String code);
    BankTokens refreshTokens(String refreshToken);
    List<BankTransaction> fetchTransactions(BankTokens tokens, LocalDate from, LocalDate to);
}
```

Adding a new bank = adding a new class in `adapter/out/bank/<provider>/` that implements `BankProviderPort`. Zero changes to the domain or application layers.

---

## Authentication

SpendStat is a single-user app. Authentication uses **username + password → JWT**.

- **Access token**: short-lived (15 min), sent in `Authorization: Bearer` header.
- **Refresh token**: long-lived (30 days), stored in secure storage on device, exchanged via `/auth/refresh`.
- Spring Security handles token validation. No third-party identity provider needed.
- Passwords are hashed with bcrypt.

---

## Deployment Strategy

### Local / Self-hosted (Docker Compose)

```yaml
services:
  db:       PostgreSQL 15
  backend:  Spring Boot JAR (Dockerfile)
  # Mobile app is distributed via Expo / app stores, not containerised
```

The backend is deployed as a Docker container. The mobile app is distributed via Expo Go (development) or compiled to APK/IPA for production.

### Self-hosted production checklist
- HTTPS via a reverse proxy (nginx + Let's Encrypt)
- Env vars for secrets (DB password, JWT secret, bank API credentials) — never committed to repo
- Daily DB backups (pg_dump → local or object storage)
