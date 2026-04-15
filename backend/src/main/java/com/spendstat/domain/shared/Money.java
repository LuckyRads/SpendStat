package com.spendstat.domain.shared;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
public final class Money {

    private final BigDecimal amount;
    private final CurrencyCode currency;

    private Money(BigDecimal amount, CurrencyCode currency) {
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, CurrencyCode currency) {
        return new Money(amount, currency);
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, CurrencyCode.of(currencyCode));
    }

    public static Money zero(CurrencyCode currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money negate() {
        return new Money(amount.negate(), currency);
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
