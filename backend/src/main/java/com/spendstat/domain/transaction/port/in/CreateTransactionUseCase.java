package com.spendstat.domain.transaction.port.in;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.transaction.Transaction;
import com.spendstat.domain.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface CreateTransactionUseCase {

    record CreateTransactionCommand(
            UserId userId,
            UUID accountId,    // nullable
            UUID categoryId,   // nullable
            BigDecimal amount,
            String currency,
            TransactionType type,
            String description,
            String merchant,
            LocalDate txDate
    ) {
    }

    Transaction createTransaction(CreateTransactionCommand command);
}
