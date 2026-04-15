package com.spendstat.adapter.out.persistence.user;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.user.RefreshToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String tokenValue;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Instant createdAt;

    public static RefreshTokenJpaEntity fromDomain(RefreshToken token) {
        RefreshTokenJpaEntity e = new RefreshTokenJpaEntity();
        e.id = token.getId();
        e.userId = token.getUserId().getValue();
        e.tokenValue = token.getTokenValue();
        e.expiresAt = token.getExpiresAt();
        e.createdAt = token.getCreatedAt();
        return e;
    }

    public RefreshToken toDomain() {
        return RefreshToken.from(id, UserId.of(userId), tokenValue, expiresAt, createdAt);
    }
}
