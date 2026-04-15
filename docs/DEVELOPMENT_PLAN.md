# SpendStat — Development Plan

## Table of Contents
1. [Phased Roadmap](#phased-roadmap)
2. [Phase 1 — Foundation and MVP](#phase-1--foundation-and-mvp)
3. [Phase 2 — Bank Sync and Custom Categories](#phase-2--bank-sync-and-custom-categories)
4. [Phase 3 — Advanced Analytics](#phase-3--advanced-analytics)
5. [Phase 4 — Platform Expansion](#phase-4--platform-expansion)
6. [Folder and Module Structure](#folder-and-module-structure)
7. [Development Workflow](#development-workflow)

---

## Phased Roadmap

| Phase | Theme | Estimated Duration |
|---|---|---|
| 1 | Foundation + MVP (auth, manual transactions, basic stats) | 5–7 weeks |
| 2 | Bank Sync (Revolut) + Custom Categories | 3–4 weeks |
| 3 | Advanced Analytics + Offline + Budgets | 3–4 weeks |
| 4 | Additional Banks + Web + Export | open-ended |

Durations assume part-time solo development (~10–15 hours/week).

---

## Phase 1 — Foundation and MVP

### Goal
A working end-to-end app: user registers, logs in, manually adds transactions, sees a monthly summary and a simple chart.

### Backend Tasks

#### 1.1 Project Bootstrap
- [ ] Initialise Spring Boot project via start.spring.io (Gradle Kotlin DSL, Java 21)
  - Dependencies: Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Flyway, Lombok, Validation
- [ ] Set up `application.yml` with environment variable placeholders for DB URL, JWT secret, etc.
- [ ] Create `application-local.yml` for local development values
- [ ] Add `docker-compose.yml` at repo root with PostgreSQL and pgAdmin services
- [ ] Configure Flyway; write first migration `V1__initial_schema.sql`

#### 1.2 Auth Module
- [ ] Create `User` entity and `UserRepository`
- [ ] Implement `AuthService` with register (bcrypt password) and login logic
- [ ] Implement JWT utility class: generate access token, validate, extract claims
- [ ] Implement refresh token entity, repository, and rotation logic
- [ ] Write `JwtAuthenticationFilter` (extends `OncePerRequestFilter`)
- [ ] Configure `SecurityFilterChain` — stateless, permit `/auth/**`, secure everything else
- [ ] Create `AuthController` with `/auth/register`, `/auth/login`, `/auth/refresh`, `/auth/logout`
- [ ] Unit tests for `AuthService` and JWT utility

#### 1.3 Account Module
- [ ] Create `Account` entity, `AccountRepository`
- [ ] Implement `AccountService` (CRUD, ownership check — users can only touch their own accounts)
- [ ] Create `AccountController` for REST endpoints
- [ ] Unit tests for service layer

#### 1.4 Category Module
- [ ] Create `Category` entity (nullable `user_id` for system defaults)
- [ ] Write migration `V2__seed_default_categories.sql` with standard categories
- [ ] Implement `CategoryService` (list = system + user's own; create/update/delete user categories only)
- [ ] Create `CategoryController`

#### 1.5 Transaction Module
- [ ] Create `Transaction` entity with all fields from schema
- [ ] Implement `TransactionRepository` with custom JPQL queries for:
  - Paginated list with optional filters (account, category, date range, type)
  - Sum by category for a given period
  - Daily/monthly balance aggregation
- [ ] Implement `TransactionService` (CRUD + balance recalculation awareness)
- [ ] Create `TransactionController`
- [ ] Unit and integration tests for repository queries

#### 1.6 Statistics Module
- [ ] Implement `StatisticsService` with methods:
  - `getSummary(userId, from, to)` — total income, expenses, net
  - `getByCategory(userId, from, to)` — list of `{categoryId, name, totalAmount}`
  - `getBalanceHistory(userId, accountId?, granularity)` — list of `{date, balance}` data points
- [ ] Create `StatisticsController`
- [ ] Test with realistic data scenarios

#### 1.7 Cross-Cutting
- [ ] Global `@ExceptionHandler` (`@ControllerAdvice`) returning consistent error JSON
- [ ] Request validation with Bean Validation annotations; propagate `MethodArgumentNotValidException`
- [ ] CORS configuration bean
- [ ] Basic `SecurityAuditLog` (optional for MVP but cheap to add early)

---

### Mobile Tasks

#### 1.8 Project Bootstrap
- [ ] Initialise Expo project: `npx create-expo-app spendstat-mobile --template` (TypeScript template)
- [ ] Install core dependencies:
  - `axios` — HTTP client
  - `expo-secure-store` — secure JWT storage
  - `zustand` — state management
  - `@react-navigation/native` + Expo Router — navigation
  - `react-hook-form` + `zod` — form handling and validation
  - `victory-native` or `react-native-gifted-charts` — charts
  - `date-fns` — date utilities
- [ ] Set up ESLint (eslint-config-expo) and Prettier
- [ ] Configure absolute imports via `tsconfig.json` `paths`
- [ ] Create `src/api/client.ts` — Axios instance with base URL, request interceptor to attach Bearer token, response interceptor to handle 401 / token refresh

#### 1.9 Auth Flow
- [ ] `AuthStore` (Zustand) — holds `accessToken`, `refreshToken`, `user`; persists to SecureStore
- [ ] Register screen with email + password form (react-hook-form + zod validation)
- [ ] Login screen
- [ ] Auto-login on app launch if valid tokens exist
- [ ] Logout action clears store and SecureStore

#### 1.10 Main Navigation
- [ ] Tab navigator: Dashboard | Transactions | Add | Accounts | Settings
- [ ] Shared header component
- [ ] Loading state skeleton screens

#### 1.11 Transactions Screen
- [ ] `TransactionStore` (Zustand) — list, current filters, pagination state
- [ ] Transaction list component with FlatList, pull-to-refresh, infinite scroll
- [ ] Filter sheet (bottom sheet) for date range, account, category
- [ ] Transaction list item component with amount colour coding (red/green)

#### 1.12 Add/Edit Transaction Screen
- [ ] Form: amount, description, date, account picker, category picker, type toggle
- [ ] DateTimePicker integration
- [ ] Submit to POST `/api/v1/transactions`; update local store optimistically

#### 1.13 Dashboard Screen
- [ ] Summary card: income / expenses / net for current month
- [ ] Category bar chart (Victory Native)
- [ ] Recent transactions list (last 5, link to full list)

#### 1.14 Accounts Screen
- [ ] Account list with computed balance
- [ ] Create/edit account form (name, currency, initial balance)

---

### Phase 1 Definition of Done
- User can register, log in, and persist session across restarts
- User can create accounts and manually add income/expense transactions
- Dashboard shows correct monthly summary and category chart
- All API routes have integration tests passing
- App runs on both iOS simulator and Android emulator

---

## Phase 2 — Bank Sync and Custom Categories

### Goal
Connect Revolut, auto-import transactions, support custom categories, support transfer transactions.

### Backend Tasks

#### 2.1 Bank Integration Infrastructure
- [ ] Define `BankProvider` interface
- [ ] Create `BankProviderRegistry` Spring bean
- [ ] Add `bank_connections` and `bank_sync_logs` tables (Flyway migration)
- [ ] Implement `BankConnectionService` — orchestrates consent, token storage, sync dispatch
- [ ] Implement `BankSyncService` — fetches accounts + transactions from provider, maps to domain model, deduplicates and persists
- [ ] Implement token encryption utility (AES-256-GCM) and wire into `BankConnectionService`

#### 2.2 Revolut Provider
- [ ] Register app in Revolut Open Banking Developer Portal, obtain client credentials
- [ ] Implement `RevolutBankProvider`:
  - `initiateConsent` — build authorisation URL with PKCE and state nonce
  - `exchangeCode` — call Revolut token endpoint
  - `refreshTokens` — refresh OAuth tokens
  - `fetchAccounts` — map Revolut Account objects to `BankAccount` DTO
  - `fetchTransactions` — map Revolut Transaction objects to `BankTransaction` DTO
- [ ] Implement `BankConnectionController` with initiate, callback, list, disconnect, manual-sync endpoints
- [ ] Configure callback redirect URI in application config and Revolut portal
- [ ] Add `@Scheduled` sync job (every 6 hours); handle token refresh and error backoff

#### 2.3 Custom Categories
- [ ] Enable POST/PUT/DELETE `/api/v1/categories` for user-owned categories
- [ ] Add Flyway migration for any schema changes needed
- [ ] Update category service to correctly scope queries

#### 2.4 Transfer Transactions
- [ ] Support `type = TRANSFER` in transaction entity with optional `linkedTransactionId`
- [ ] Create both debit and credit legs in `TransactionService.createTransfer()`

### Mobile Tasks

#### 2.5 Bank Connection UI
- [ ] "Connect Bank" section in Settings screen
- [ ] "Connect Revolut" button — calls initiate endpoint, opens result URL in `expo-web-browser`
- [ ] Handle deep-link callback from OAuth redirect back into the app
- [ ] Display connected bank with last-synced timestamp
- [ ] Manual sync button and disconnect option

#### 2.6 Sync Status UX
- [ ] Show sync status indicator on Dashboard
- [ ] Show "Bank" badge on bank-synced transactions in list
- [ ] Pull-to-refresh triggers a manual sync if a bank is connected

#### 2.7 Custom Category Management
- [ ] Category management screen under Settings
- [ ] Colour picker and emoji/icon picker for new categories

---

### Phase 2 Definition of Done
- User can connect Revolut, import transactions, and see them alongside manual ones
- Auto-sync runs on schedule with no duplicates
- User can create and assign custom categories
- Transfer transactions appear correctly on both accounts

---

## Phase 3 — Advanced Analytics

### Goal
Richer statistics, month-over-month trends, budget limits, and offline-first data access.

### Backend Tasks

#### 3.1 Enhanced Statistics
- [ ] `GET /api/v1/statistics/trends` — month-over-month comparison endpoint
- [ ] Optional account filter on all statistics endpoints
- [ ] Year-over-year aggregate queries

#### 3.2 Budgets
- [ ] `budgets` table: `{id, user_id, category_id, amount_limit, period_type (MONTHLY), currency}`
- [ ] `BudgetService` — CRUD + `getProgress(userId, month)` which computes spend vs. limit
- [ ] `GET /api/v1/budgets` and `GET /api/v1/budgets/progress?month=2024-11`

### Mobile Tasks

#### 3.3 Trends Screen
- [ ] New "Trends" tab or expandable section in Dashboard
- [ ] Bar chart: last 6 months of expenses side by side
- [ ] Month-over-month delta indicator per category

#### 3.4 Budgets Screen
- [ ] Budget list with progress bars (spent / limit)
- [ ] Create/edit budget form
- [ ] Visual alert when budget is exceeded or close to limit

#### 3.5 Offline Support
- [ ] Integrate `react-query` (TanStack Query) with `mmkv` persistence layer for response caching
- [ ] Show cached data with "offline" indicator when network is unavailable
- [ ] Queue manual transaction creates when offline; flush when connectivity returns

---

## Phase 4 — Platform Expansion

### Tasks (Outline)
- [ ] Second bank provider (e.g., Wise or Monzo) implementing `BankProvider`
- [ ] Multi-currency: store exchange rates (ECB API), show amounts in user's base currency
- [ ] CSV export endpoint and in-app share sheet
- [ ] Optional React web frontend (Vite + React, reusing API)
- [ ] Recurring transaction detection (heuristic: same merchant, same amount, roughly monthly)
- [ ] EAS Build + EAS Submit for App Store / Play Store distribution

---

## Folder and Module Structure

### Backend (`backend/`)

The backend uses **Hexagonal Architecture with DDD**. See `docs/ARCHITECTURE.md → Backend Architecture` for the full design rationale and dependency rules.

```
backend/
├── src/main/java/com/spendstat/
│   │
│   ├── domain/                              ← pure Java, zero framework dependencies
│   │   ├── shared/
│   │   │   ├── Money.java                   (value object)
│   │   │   ├── CurrencyCode.java            (value object)
│   │   │   └── UserId.java                  (value object)
│   │   ├── transaction/
│   │   │   ├── Transaction.java             (aggregate root)
│   │   │   ├── TransactionId.java
│   │   │   ├── TransactionType.java         (enum: INCOME, EXPENSE, TRANSFER)
│   │   │   └── port/
│   │   │       ├── in/
│   │   │       │   ├── CreateTransactionUseCase.java
│   │   │       │   ├── UpdateTransactionUseCase.java
│   │   │       │   ├── DeleteTransactionUseCase.java
│   │   │       │   └── QueryTransactionsUseCase.java
│   │   │       └── out/
│   │   │           └── TransactionRepository.java
│   │   ├── account/
│   │   │   ├── Account.java
│   │   │   ├── AccountId.java
│   │   │   └── port/
│   │   │       ├── in/
│   │   │       │   ├── CreateAccountUseCase.java
│   │   │       │   ├── UpdateAccountUseCase.java
│   │   │       │   └── QueryAccountsUseCase.java
│   │   │       └── out/
│   │   │           └── AccountRepository.java
│   │   ├── category/
│   │   │   ├── Category.java
│   │   │   ├── CategoryId.java
│   │   │   └── port/
│   │   │       ├── in/
│   │   │       │   └── ManageCategoriesUseCase.java
│   │   │       └── out/
│   │   │           └── CategoryRepository.java
│   │   ├── statistics/
│   │   │   └── port/
│   │   │       ├── in/
│   │   │       │   └── GetStatisticsUseCase.java
│   │   │       └── out/
│   │   │           └── StatisticsQueryPort.java
│   │   └── bank/
│   │       ├── BankConnection.java
│   │       ├── BankConnectionId.java
│   │       └── port/
│   │           ├── in/
│   │           │   ├── ConnectBankUseCase.java
│   │           │   └── SyncBankTransactionsUseCase.java
│   │           └── out/
│   │               ├── BankConnectionRepository.java
│   │               └── BankProviderPort.java
│   │
│   ├── application/                         ← use case implementations, Spring @Service beans
│   │   ├── transaction/
│   │   │   └── TransactionService.java      (implements all Transaction inbound ports)
│   │   ├── account/
│   │   │   └── AccountService.java
│   │   ├── category/
│   │   │   └── CategoryService.java
│   │   ├── statistics/
│   │   │   └── StatisticsService.java
│   │   └── bank/
│   │       └── BankSyncService.java
│   │
│   └── adapter/
│       ├── in/                              ← driving adapters (Spring MVC, @Scheduled)
│       │   ├── web/
│       │   │   ├── TransactionController.java
│       │   │   ├── AccountController.java
│       │   │   ├── CategoryController.java
│       │   │   ├── StatisticsController.java
│       │   │   ├── AuthController.java
│       │   │   ├── BankConnectionController.java
│       │   │   └── dto/
│       │   │       ├── CreateTransactionRequest.java
│       │   │       ├── TransactionResponse.java
│       │   │       └── ...
│       │   └── scheduler/
│       │       └── BankSyncScheduler.java
│       └── out/                             ← driven adapters (JPA, HTTP clients)
│           ├── persistence/
│           │   ├── transaction/
│           │   │   ├── TransactionPersistenceAdapter.java
│           │   │   ├── TransactionJpaRepository.java
│           │   │   └── TransactionJpaEntity.java
│           │   ├── account/
│           │   │   ├── AccountPersistenceAdapter.java
│           │   │   ├── AccountJpaRepository.java
│           │   │   └── AccountJpaEntity.java
│           │   ├── category/
│           │   │   ├── CategoryPersistenceAdapter.java
│           │   │   ├── CategoryJpaRepository.java
│           │   │   └── CategoryJpaEntity.java
│           │   └── user/
│           │       ├── UserPersistenceAdapter.java
│           │       ├── UserJpaRepository.java
│           │       └── UserJpaEntity.java
│           └── bank/
│               └── revolut/
│                   ├── RevolutBankAdapter.java   (implements BankProviderPort)
│                   └── RevolutApiClient.java
│
├── src/main/java/com/spendstat/infrastructure/
│   ├── security/
│   │   ├── SecurityConfig.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── JwtUtils.java
│   └── config/
│       └── CorsConfig.java
│
├── src/main/resources/
│   ├── application.yml
│   ├── application-local.yml           (gitignored)
│   └── db/migration/
│       ├── V1__initial_schema.sql
│       ├── V2__seed_default_categories.sql
│       └── V3__bank_connections.sql
└── build.gradle.kts
```

### Mobile (`mobile/`)

```
mobile/
├── app/
│   ├── (auth)/
│   │   ├── login.tsx
│   │   └── register.tsx
│   ├── (tabs)/
│   │   ├── index.tsx                   (Dashboard)
│   │   ├── transactions.tsx
│   │   ├── add.tsx
│   │   ├── accounts.tsx
│   │   └── settings.tsx
│   └── _layout.tsx
├── src/
│   ├── api/
│   │   ├── client.ts                   (Axios instance + interceptors)
│   │   ├── auth.ts
│   │   ├── transactions.ts
│   │   ├── accounts.ts
│   │   ├── categories.ts
│   │   ├── statistics.ts
│   │   └── bank.ts
│   ├── store/
│   │   ├── authStore.ts                (Zustand)
│   │   ├── transactionStore.ts
│   │   └── accountStore.ts
│   ├── components/
│   │   ├── TransactionListItem.tsx
│   │   ├── SummaryCard.tsx
│   │   ├── CategoryChart.tsx
│   │   └── BalanceChart.tsx
│   └── types/
│       └── index.ts                    (shared TypeScript types)
├── app.json
├── tsconfig.json
└── package.json
```

---

## Development Workflow

### Local Setup

1. **Start the database:**
   ```bash
   docker-compose up -d db
   ```

2. **Run the backend:**
   ```bash
   cd backend
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```
   The API will be available at `http://localhost:8080`.

3. **Run the mobile app:**
   ```bash
   cd mobile
   npx expo start
   ```
   Scan the QR code with Expo Go (Android/iOS), or press `i`/`a` to open a simulator.

### Running Tests

```bash
# Backend unit + integration tests
cd backend && ./gradlew test

# Mobile type-check
cd mobile && npx tsc --noEmit
```

### Environment Variables (Backend)

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | JDBC URL for PostgreSQL | `jdbc:postgresql://localhost:5432/spendstat` |
| `DB_USERNAME` | DB user | `spendstat` |
| `DB_PASSWORD` | DB password | (secret) |
| `JWT_SECRET` | HS256 signing secret (32+ chars) | (secret) |
| `JWT_EXPIRY_MINUTES` | Access token TTL | `15` |
| `REFRESH_TOKEN_EXPIRY_DAYS` | Refresh token TTL | `30` |
| `REVOLUT_CLIENT_ID` | Revolut Open Banking client ID | (from portal) |
| `REVOLUT_CLIENT_SECRET` | Revolut Open Banking client secret | (secret) |
| `REVOLUT_REDIRECT_URI` | OAuth callback URL | `https://yourhost/api/v1/bank/revolut/callback` |

Never commit secrets; use `.env` files or a secrets manager.

### Git Branching

- `main` — stable, always deployable
- `feature/<name>` — new features (merged via PR)
- `fix/<name>` — bug fixes
- `chore/<name>` — maintenance (deps, config, docs)
