package com.spendstat.domain.category;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public final class CategoryId {

    private final UUID value;

    private CategoryId(UUID value) {
        this.value = value;
    }

    public static CategoryId of(UUID value) {
        if (value == null) throw new IllegalArgumentException("CategoryId cannot be null");
        return new CategoryId(value);
    }

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
