package com.spendstat.domain.user.port.in;

import com.spendstat.domain.user.TokenPair;

public interface LoginUseCase {

    record LoginCommand(String email, String password) {
    }

    TokenPair login(LoginCommand command);
}
