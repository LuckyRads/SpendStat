package com.spendstat.domain.account;

import com.spendstat.domain.shared.Money;
import com.spendstat.domain.shared.UserId;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Account {

    private final AccountId id;
    private final UserId userId;
    private String name;
    private final Money initialBalance;
    private final Instant createdAt;

    private Account(AccountId id, UserId userId, String name, Money initialBalance, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.initialBalance = initialBalance;
        this.createdAt = createdAt;
    }

    public static Account init(UserId userId, String name, Money initialBalance) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Account name cannot be blank");
        return new Account(AccountId.generate(), userId, name, initialBalance, Instant.now());
    }

    public static Account from(AccountId id, UserId userId, String name, Money initialBalance, Instant createdAt) {
        return new Account(id, userId, name, initialBalance, createdAt);
    }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("Account name cannot be blank");
        this.name = newName;
    }

    public boolean isOwnedBy(UserId userId) {
        return this.userId.equals(userId);
    }
}
