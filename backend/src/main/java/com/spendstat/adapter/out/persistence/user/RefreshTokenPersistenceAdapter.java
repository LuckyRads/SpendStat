package com.spendstat.adapter.out.persistence.user;

import com.spendstat.domain.user.RefreshToken;
import com.spendstat.domain.user.port.out.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public RefreshToken save(RefreshToken token) {
        return jpaRepository.save(RefreshTokenJpaEntity.fromDomain(token)).toDomain();
    }

    @Override
    public Optional<RefreshToken> findByTokenValue(String tokenValue) {
        return jpaRepository.findByTokenValue(tokenValue).map(RefreshTokenJpaEntity::toDomain);
    }

    @Override
    public void deleteByTokenValue(String tokenValue) {
        jpaRepository.deleteByTokenValue(tokenValue);
    }
}
