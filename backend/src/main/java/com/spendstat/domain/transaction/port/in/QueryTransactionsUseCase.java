package com.spendstat.domain.transaction.port.in;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.transaction.Transaction;
import com.spendstat.domain.transaction.TransactionFilter;
import com.spendstat.domain.transaction.TransactionId;
import com.spendstat.domain.transaction.TransactionPage;

public interface QueryTransactionsUseCase {

    TransactionPage queryTransactions(TransactionFilter filter);

    Transaction getTransactionById(TransactionId id, UserId userId);
}
