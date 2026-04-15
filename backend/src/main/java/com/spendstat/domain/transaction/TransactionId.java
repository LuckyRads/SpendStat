package com.spendstat.domain.transaction;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public final class TransactionId {

    private final UUID value;

    private TransactionId(UUID value) {
        this.value = value;
    }

    public static TransactionId of(UUID value) {
        if (value == null) throw new IllegalArgumentException("TransactionId cannot be null");
        return new TransactionId(value);
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
