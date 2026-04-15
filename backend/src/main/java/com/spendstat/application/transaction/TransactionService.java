package com.spendstat.application.transaction;

import com.spendstat.application.exception.ResourceNotFoundException;
import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.category.CategoryId;
import com.spendstat.domain.shared.Money;
import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.transaction.Transaction;
import com.spendstat.domain.transaction.TransactionFilter;
import com.spendstat.domain.transaction.TransactionId;
import com.spendstat.domain.transaction.TransactionPage;
import com.spendstat.domain.transaction.port.in.CreateTransactionUseCase;
import com.spendstat.domain.transaction.port.in.DeleteTransactionUseCase;
import com.spendstat.domain.transaction.port.in.QueryTransactionsUseCase;
import com.spendstat.domain.transaction.port.in.UpdateTransactionUseCase;
import com.spendstat.domain.transaction.port.out.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService implements CreateTransactionUseCase, UpdateTransactionUseCase,
        DeleteTransactionUseCase, QueryTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction createTransaction(CreateTransactionCommand command) {
        AccountId accountId = command.accountId() != null ? AccountId.of(command.accountId()) : null;
        CategoryId categoryId = command.categoryId() != null ? CategoryId.of(command.categoryId()) : null;
        Money amount = Money.of(command.amount(), command.currency());

        Transaction transaction = Transaction.initManual(
                command.userId(), accountId, categoryId, amount,
                command.type(), command.description(), command.merchant(), command.txDate()
        );
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(UpdateTransactionCommand command) {
        Transaction transaction = requireOwned(command.transactionId(), command.userId());
        CategoryId categoryId = command.categoryId() != null ? CategoryId.of(command.categoryId()) : null;
        transaction.update(categoryId, command.description(), command.merchant());
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(TransactionId id, UserId userId) {
        requireOwned(id, userId);
        transactionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionPage queryTransactions(TransactionFilter filter) {
        return transactionRepository.findByFilter(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getTransactionById(TransactionId id, UserId userId) {
        return requireOwned(id, userId);
    }

    private Transaction requireOwned(TransactionId id, UserId userId) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id.getValue()));
        if (!tx.isOwnedBy(userId)) {
            throw new ResourceNotFoundException("Transaction", id.getValue());
        }
        return tx;
    }
}
