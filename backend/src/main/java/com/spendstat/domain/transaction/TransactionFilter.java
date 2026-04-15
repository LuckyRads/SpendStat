package com.spendstat.domain.transaction;

import com.spendstat.domain.shared.UserId;

import java.time.LocalDate;
import java.util.UUID;

public record TransactionFilter(
        UserId userId,
        UUID accountId,      // nullable
        UUID categoryId,     // nullable
        TransactionType type, // nullable
        LocalDate from,      // nullable
        LocalDate to,        // nullable
        int page,
        int size
) {
    public static TransactionFilter of(UserId userId, int page, int size) {
        return new TransactionFilter(userId, null, null, null, null, null, page, size);
    }
}
