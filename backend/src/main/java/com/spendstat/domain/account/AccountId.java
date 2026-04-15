package com.spendstat.domain.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public final class AccountId {

    private final UUID value;

    private AccountId(UUID value) {
        this.value = value;
    }

    public static AccountId of(UUID value) {
        if (value == null) throw new IllegalArgumentException("AccountId cannot be null");
        return new AccountId(value);
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
