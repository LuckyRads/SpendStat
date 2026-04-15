package com.spendstat.adapter.in.web.dto.transaction;

import com.spendstat.domain.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTransactionRequest(
        UUID accountId,   // nullable

        UUID categoryId,  // nullable

        @NotNull(message = "Amount is required")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "[A-Z]{3}", message = "Currency must be a 3-letter ISO-4217 code")
        String currency,

        @NotNull(message = "Transaction type is required")
        TransactionType type,

        String description,

        String merchant,

        @NotNull(message = "Transaction date is required")
        LocalDate txDate
) {
}
