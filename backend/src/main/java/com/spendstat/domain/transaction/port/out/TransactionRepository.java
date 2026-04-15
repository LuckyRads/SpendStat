package com.spendstat.domain.transaction.port.out;

import com.spendstat.domain.transaction.Transaction;
import com.spendstat.domain.transaction.TransactionFilter;
import com.spendstat.domain.transaction.TransactionId;
import com.spendstat.domain.transaction.TransactionPage;

import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(TransactionId id);

    TransactionPage findByFilter(TransactionFilter filter);

    void deleteById(TransactionId id);
}
