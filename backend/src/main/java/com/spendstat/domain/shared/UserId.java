package com.spendstat.domain.shared;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public final class UserId {

    private final UUID value;

    private UserId(UUID value) {
        this.value = value;
    }

    public static UserId of(UUID value) {
        if (value == null) throw new IllegalArgumentException("UserId cannot be null");
        return new UserId(value);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
