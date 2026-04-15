package com.spendstat.domain.user.port.out;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.user.Email;
import com.spendstat.domain.user.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
