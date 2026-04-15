package com.spendstat.domain.user;

import com.spendstat.domain.shared.UserId;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class RefreshToken {

    private final UUID id;
    private final UserId userId;
    private final String tokenValue;
    private final Instant expiresAt;
    private final Instant createdAt;

    private RefreshToken(UUID id, UserId userId, String tokenValue, Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenValue = tokenValue;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static RefreshToken init(UserId userId, long expiryDays) {
        return new RefreshToken(
                UUID.randomUUID(),
                userId,
                UUID.randomUUID().toString(),
                Instant.now().plusSeconds(expiryDays * 86_400L),
                Instant.now()
        );
    }

    public static RefreshToken from(UUID id, UserId userId, String tokenValue, Instant expiresAt, Instant createdAt) {
        return new RefreshToken(id, userId, tokenValue, expiresAt, createdAt);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
