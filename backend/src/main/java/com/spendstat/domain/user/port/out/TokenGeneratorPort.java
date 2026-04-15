package com.spendstat.domain.user.port.out;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.user.User;

import java.util.Optional;

public interface TokenGeneratorPort {

    String generateAccessToken(User user);

    Optional<UserId> extractUserIdFromToken(String token);
}
