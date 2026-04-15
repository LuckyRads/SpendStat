package com.spendstat.domain.shared;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class CurrencyCode {

    private final String value;

    private CurrencyCode(String value) {
        if (value == null || !value.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Currency code must be a 3-letter ISO-4217 code, got: " + value);
        }
        this.value = value;
    }

    public static CurrencyCode of(String value) {
        return new CurrencyCode(value != null ? value.toUpperCase() : null);
    }

    @Override
    public String toString() {
        return value;
    }
}
