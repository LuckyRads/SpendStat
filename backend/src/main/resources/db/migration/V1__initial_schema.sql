CREATE TABLE users
(
    id            UUID         NOT NULL PRIMARY KEY,
    email         TEXT         NOT NULL UNIQUE,
    password_hash TEXT         NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE refresh_tokens
(
    id          UUID        NOT NULL PRIMARY KEY,
    user_id     UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_value TEXT        NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);

CREATE TABLE accounts
(
    id              UUID           NOT NULL PRIMARY KEY,
    user_id         UUID           NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name            TEXT           NOT NULL,
    currency        CHAR(3)        NOT NULL,
    initial_balance NUMERIC(15, 4) NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_accounts_user_id ON accounts (user_id);

CREATE TABLE categories
(
    id         UUID        NOT NULL PRIMARY KEY,
    user_id    UUID        REFERENCES users (id) ON DELETE CASCADE, -- NULL = system default
    name       TEXT        NOT NULL,
    color      TEXT,
    icon       TEXT,
    is_default BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_categories_user_id ON categories (user_id);

CREATE TABLE transactions
(
    id          UUID           NOT NULL PRIMARY KEY,
    user_id     UUID           NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    account_id  UUID           REFERENCES accounts (id) ON DELETE SET NULL,
    category_id UUID           REFERENCES categories (id) ON DELETE SET NULL,
    external_id TEXT,
    amount      NUMERIC(15, 4) NOT NULL,
    currency    CHAR(3)        NOT NULL,
    description TEXT,
    merchant    TEXT,
    tx_date     DATE           NOT NULL,
    type        TEXT           NOT NULL, -- INCOME | EXPENSE | TRANSFER
    source      TEXT           NOT NULL DEFAULT 'MANUAL',
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    UNIQUE (account_id, external_id)
);

CREATE INDEX idx_transactions_user_id ON transactions (user_id);
CREATE INDEX idx_transactions_account_id ON transactions (account_id);
CREATE INDEX idx_transactions_category_id ON transactions (category_id);
CREATE INDEX idx_transactions_tx_date ON transactions (tx_date);
CREATE INDEX idx_transactions_user_date ON transactions (user_id, tx_date);
