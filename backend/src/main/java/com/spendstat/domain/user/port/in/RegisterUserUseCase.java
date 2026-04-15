package com.spendstat.domain.user.port.in;

import com.spendstat.domain.user.TokenPair;

public interface RegisterUserUseCase {

    record RegisterCommand(String email, String password) {
    }

    TokenPair register(RegisterCommand command);
}
