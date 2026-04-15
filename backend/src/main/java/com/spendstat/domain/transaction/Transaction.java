package com.spendstat.domain.transaction;

import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.category.CategoryId;
import com.spendstat.domain.shared.Money;
import com.spendstat.domain.shared.UserId;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Transaction {

    private final TransactionId id;
    private final UserId userId;
    private final AccountId accountId; // nullable for manual cash transactions
    private CategoryId categoryId;     // mutable — can be reassigned
    private final Money amount;        // positive for INCOME, negative for EXPENSE
    private String description;
    private String merchant;
    private final LocalDate txDate;
    private final TransactionType type;
    private final TransactionSource source;
    private final String externalId; // nullable — only for bank-synced transactions
    private final Instant createdAt;

    private Transaction(TransactionId id, UserId userId, AccountId accountId, CategoryId categoryId,
                        Money amount, String description, String merchant, LocalDate txDate,
                        TransactionType type, TransactionSource source, String externalId, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.merchant = merchant;
        this.txDate = txDate;
        this.type = type;
        this.source = source;
        this.externalId = externalId;
        this.createdAt = createdAt;
    }

    /**
     * Factory for manually entered transactions.
     * Normalises sign: EXPENSE amounts are stored as negative, INCOME as positive.
     */
    public static Transaction initManual(UserId userId, AccountId accountId, CategoryId categoryId,
                                         Money amount, TransactionType type,
                                         String description, String merchant, LocalDate txDate) {
        Money normalisedAmount = normaliseSign(amount, type);
        return new Transaction(
                TransactionId.generate(), userId, accountId, categoryId,
                normalisedAmount, description, merchant, txDate,
                type, TransactionSource.MANUAL, null, Instant.now()
        );
    }

    public static Transaction from(TransactionId id, UserId userId, AccountId accountId, CategoryId categoryId,
                                    Money amount, String description, String merchant, LocalDate txDate,
                                    TransactionType type, TransactionSource source, String externalId,
                                    Instant createdAt) {
        return new Transaction(id, userId, accountId, categoryId, amount, description, merchant,
                txDate, type, source, externalId, createdAt);
    }

    public void update(CategoryId categoryId, String description, String merchant) {
        this.categoryId = categoryId;
        this.description = description;
        this.merchant = merchant;
    }

    public boolean isOwnedBy(UserId userId) {
        return this.userId.equals(userId);
    }

    private static Money normaliseSign(Money amount, TransactionType type) {
        return switch (type) {
            case EXPENSE -> amount.isPositive() ? amount.negate() : amount;
            case INCOME  -> amount.isNegative() ? amount.negate() : amount;
            case TRANSFER -> amount; // sign is caller's responsibility for transfers
        };
    }

    // Convenience: expose raw UUID for persistence layer
    public UUID getAccountIdValue() {
        return accountId != null ? accountId.getValue() : null;
    }

    public UUID getCategoryIdValue() {
        return categoryId != null ? categoryId.getValue() : null;
    }
}
