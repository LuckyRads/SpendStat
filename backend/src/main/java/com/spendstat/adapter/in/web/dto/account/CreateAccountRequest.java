package com.spendstat.adapter.in.web.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank(message = "Account name is required")
        String name,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "[A-Z]{3}", message = "Currency must be a 3-letter ISO-4217 code (e.g. EUR)")
        String currency,

        @NotNull(message = "Initial balance is required")
        @PositiveOrZero(message = "Initial balance cannot be negative")
        BigDecimal initialBalance
) {
}
