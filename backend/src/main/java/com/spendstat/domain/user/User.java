package com.spendstat.domain.user;

import com.spendstat.domain.shared.UserId;
import lombok.Getter;

import java.time.Instant;

@Getter
public class User {

    private final UserId id;
    private final Email email;
    private final String passwordHash;
    private final Instant createdAt;

    private User(UserId id, Email email, String passwordHash, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public static User init(Email email, String passwordHash) {
        return new User(UserId.generate(), email, passwordHash, Instant.now());
    }

    public static User from(UserId id, Email email, String passwordHash, Instant createdAt) {
        return new User(id, email, passwordHash, createdAt);
    }
}
