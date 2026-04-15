package com.spendstat.adapter.out.persistence.user;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.user.Email;
import com.spendstat.domain.user.User;
import com.spendstat.domain.user.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        return jpaRepository.save(UserJpaEntity.fromDomain(user)).toDomain();
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.getValue()).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue()).map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }
}
