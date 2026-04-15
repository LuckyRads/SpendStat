package com.spendstat.adapter.in.web.dto.transaction;

import com.spendstat.domain.transaction.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID accountId,
        UUID categoryId,
        BigDecimal amount,
        String currency,
        String type,
        String source,
        String description,
        String merchant,
        LocalDate txDate,
        Instant createdAt
) {
    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.getId().getValue(),
                tx.getAccountIdValue(),
                tx.getCategoryIdValue(),
                tx.getAmount().getAmount(),
                tx.getAmount().getCurrency().getValue(),
                tx.getType().name(),
                tx.getSource().name(),
                tx.getDescription(),
                tx.getMerchant(),
                tx.getTxDate(),
                tx.getCreatedAt()
        );
    }
}
