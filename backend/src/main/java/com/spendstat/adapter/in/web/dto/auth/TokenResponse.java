package com.spendstat.adapter.in.web.dto.auth;

import com.spendstat.domain.user.TokenPair;

public record TokenResponse(String accessToken, String refreshToken) {

    public static TokenResponse from(TokenPair pair) {
        return new TokenResponse(pair.accessToken(), pair.refreshToken());
    }
}
