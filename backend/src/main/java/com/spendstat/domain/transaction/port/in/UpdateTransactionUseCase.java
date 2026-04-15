package com.spendstat.domain.transaction.port.in;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.transaction.Transaction;
import com.spendstat.domain.transaction.TransactionId;

import java.util.UUID;

public interface UpdateTransactionUseCase {

    record UpdateTransactionCommand(
            TransactionId transactionId,
            UserId userId,
            UUID categoryId, // nullable
            String description,
            String merchant
    ) {
    }

    Transaction updateTransaction(UpdateTransactionCommand command);
}
