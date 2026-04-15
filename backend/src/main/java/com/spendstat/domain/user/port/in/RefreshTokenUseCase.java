package com.spendstat.domain.user.port.in;

import com.spendstat.domain.user.TokenPair;

public interface RefreshTokenUseCase {

    TokenPair refresh(String refreshToken);
}
