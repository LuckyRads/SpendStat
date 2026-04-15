package com.spendstat.adapter.in.web.dto.account;

import com.spendstat.domain.account.Account;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String name,
        String currency,
        BigDecimal initialBalance,
        Instant createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId().getValue(),
                account.getName(),
                account.getInitialBalance().getCurrency().getValue(),
                account.getInitialBalance().getAmount(),
                account.getCreatedAt()
        );
    }
}
