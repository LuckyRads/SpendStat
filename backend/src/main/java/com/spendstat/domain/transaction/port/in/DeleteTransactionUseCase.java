package com.spendstat.domain.transaction.port.in;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.transaction.TransactionId;

public interface DeleteTransactionUseCase {

    void deleteTransaction(TransactionId id, UserId userId);
}
