package com.spendstat.domain.user.port.in;

public interface LogoutUseCase {

    void logout(String refreshToken);
}
