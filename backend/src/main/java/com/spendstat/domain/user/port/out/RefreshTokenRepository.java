package com.spendstat.domain.user.port.out;

import com.spendstat.domain.user.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByTokenValue(String tokenValue);

    void deleteByTokenValue(String tokenValue);
}
