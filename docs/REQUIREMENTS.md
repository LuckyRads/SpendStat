# SpendStat — Requirements

## Table of Contents
1. [Functional Requirements](#functional-requirements)
2. [Non-Functional Requirements](#non-functional-requirements)
3. [User Stories](#user-stories)
4. [MVP Scope](#mvp-scope)
5. [Future Scope](#future-scope)

---

## Functional Requirements

### FR-01: User Account Management
- The system shall allow a user to register with an email address and password.
- The system shall allow a user to log in and receive a JWT access token and refresh token.
- The system shall allow a user to log out, revoking their refresh token.
- The system shall allow a user to update their display name.

### FR-02: Manual Transaction Entry
- The user shall be able to create a transaction with: amount, currency, date, category, account, and optional description/note.
- The user shall be able to edit any field of a manually entered transaction.
- The user shall be able to delete a manually entered transaction.
- Transactions shall be classified as income, expense, or transfer.

### FR-03: Account Management
- The user shall be able to create one or more named accounts (e.g., "Cash wallet", "Personal checking").
- Each account shall have a currency and an optional initial balance.
- The user shall be able to edit or soft-delete accounts.
- Balance shall be computed dynamically from the sum of transactions plus the initial balance.

### FR-04: Category Management
- The system shall provide a default set of spending categories (Food, Transport, Housing, Entertainment, Health, Income, Other).
- The user shall be able to create custom categories with a name, icon, and colour.
- The user shall be able to edit or delete custom categories.
- Deleting a category shall uncategorise its transactions (set to null / "Uncategorised"), not delete them.

### FR-05: Revolut Bank Sync
- The user shall be able to initiate an OAuth consent flow to connect their Revolut account.
- Upon successful connection, the system shall import the last 90 days of transactions.
- The system shall automatically re-sync Revolut transactions every 6 hours.
- The user shall be able to manually trigger a re-sync at any time.
- Re-syncing shall never create duplicate transactions.
- The user shall be able to disconnect their Revolut account, which removes the connection and stops future syncs (imported transactions are retained).

### FR-06: Statistics and Analytics
- The system shall display a summary of total income, total expenses, and net balance for a user-selected period (this week / this month / last month / custom range).
- The system shall display a category breakdown (pie or bar chart) for expenses in a selected period.
- The system shall display a running balance chart over time.
- The system shall display a month-over-month comparison of spending.

### FR-07: Transaction List and Search
- The user shall be able to view a paginated, reverse-chronological list of all transactions.
- The user shall be able to filter transactions by: account, category, date range, and transaction type (income/expense/transfer).
- The user shall be able to search transactions by description text.

---

## Non-Functional Requirements

### NFR-01: Performance
- The transaction list API shall return the first page of results within 300ms under normal conditions.
- Statistics queries for a 1-year range shall return within 1 second.
- The mobile app shall reach interactive state within 2 seconds on a mid-range device on 4G.

### NFR-02: Security
- All API traffic shall use HTTPS/TLS.
- Passwords shall be stored as bcrypt hashes (cost factor >= 12).
- Bank OAuth tokens shall be stored encrypted at rest (AES-256-GCM).
- JWT access tokens shall expire after 15 minutes.
- Refresh tokens shall be rotated on every use.
- The app shall implement certificate pinning for production API calls (stretch goal).

### NFR-03: Reliability
- Bank sync failures shall be logged and retried; a single failure shall not break the application.
- The system shall handle Revolut API rate limits gracefully with exponential backoff.
- The database shall be backed up at least daily in a production deployment.

### NFR-04: Usability
- The mobile UI shall support both light and dark mode.
- The app shall work offline for viewing cached data; sync shall occur when connectivity is restored.
- Error messages presented to the user shall be human-readable, not stack traces.

### NFR-05: Maintainability
- Backend code shall follow standard Java/Spring conventions and have meaningful test coverage for service and controller layers.
- The mobile codebase shall use TypeScript strict mode.
- All database schema changes shall be managed by Flyway migrations.

### NFR-06: Portability
- The mobile app shall run on Android 10+ and iOS 15+.
- The backend shall run in a Docker container and not depend on host-specific configuration.

---

## User Stories

### Authentication
- **US-001**: As a new user, I want to register with my email and a password so that I can create a personal account.
- **US-002**: As a returning user, I want to log in with my credentials so that I can access my financial data.
- **US-003**: As a logged-in user, I want to stay logged in across app restarts without re-entering my password, so the experience is seamless.

### Transactions
- **US-004**: As a user, I want to quickly add a manual expense so that I can track cash spending in real time.
- **US-005**: As a user, I want to assign a category to each transaction so that I can see where my money goes.
- **US-006**: As a user, I want to edit or delete a transaction I entered incorrectly so that my data stays accurate.
- **US-007**: As a user, I want to scroll through my full transaction history and filter by date range so that I can review past spending.

### Bank Sync
- **US-008**: As a user, I want to connect my Revolut account once so that my card transactions are imported automatically without manual entry.
- **US-009**: As a user, I want imported transactions to be synced regularly in the background so that my balance is always current.
- **US-010**: As a user, I want to see when my bank account was last synced so that I know how fresh the data is.
- **US-011**: As a user, I want to disconnect my bank account at any time so that I retain control over what data is accessed.

### Analytics
- **US-012**: As a user, I want to see a monthly summary of my income vs. expenses so that I know if I am living within my means.
- **US-013**: As a user, I want a pie chart of spending by category for this month so that I can identify my biggest cost areas.
- **US-014**: As a user, I want to see my account balance charted over time so that I can observe saving or spending trends.
- **US-015**: As a user, I want to compare this month's spending to last month's so that I can see whether I am improving.

### Accounts
- **US-016**: As a user, I want to track multiple accounts (e.g., a current account and a cash wallet) so that I have a full picture of my finances.
- **US-017**: As a user, I want to set an initial balance when creating a manual account so that the balance calculation is correct from the start.

---

## MVP Scope

The MVP delivers the core personal finance loop: enter transactions, see your balance, view basic stats.

### Included in MVP
- User registration and login (JWT auth)
- Manual account creation (single currency)
- Manual transaction entry (income and expense)
- Default categories (no custom categories in MVP)
- Transaction list with basic date-range filter
- Monthly summary: total income, total expenses, net
- Spending by category (current month, bar or pie chart)
- Basic balance history chart (monthly data points)
- Backend: Spring Boot REST API + PostgreSQL
- Mobile: React Native Expo app (iOS and Android)
- Local Docker Compose for development

### Explicitly Excluded from MVP
- Revolut / bank sync (Phase 2)
- Custom categories (Phase 2)
- Transfer transactions between accounts (Phase 2)
- Month-over-month trend comparison (Phase 3)
- Offline-first / caching (Phase 3)
- Multiple currencies per account (Phase 3)
- Push notifications for sync (Phase 3)
- Web interface (future)

---

## Future Scope

### Phase 2 — Bank Sync
- Revolut OAuth 2.0 integration and auto-sync
- Custom categories with icon and colour
- Transfer transaction type
- Improved deduplication of manual + bank transactions

### Phase 3 — Advanced Analytics
- Month-over-month and year-over-year trends
- Spending forecast / budget feature
- Per-category budget limits with alerts
- Offline-first architecture with background sync
- Multiple currencies with exchange rate conversion

### Phase 4 — Platform Expansion
- Additional PSD2 bank providers (Monzo, N26, Wise)
- Optional web dashboard (React)
- CSV / PDF export of transaction history
- Recurring transaction detection
- Multi-device token management