package com.spendstat.adapter.out.persistence.user;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.user.Email;
import com.spendstat.domain.user.User;
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
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Instant createdAt;

    public static UserJpaEntity fromDomain(User user) {
        UserJpaEntity e = new UserJpaEntity();
        e.id = user.getId().getValue();
        e.email = user.getEmail().getValue();
        e.passwordHash = user.getPasswordHash();
        e.createdAt = user.getCreatedAt();
        return e;
    }

    public User toDomain() {
        return User.from(UserId.of(id), Email.of(email), passwordHash, createdAt);
    }
}
