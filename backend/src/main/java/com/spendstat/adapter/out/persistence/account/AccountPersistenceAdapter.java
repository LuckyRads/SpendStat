package com.spendstat.adapter.out.persistence.account;

import com.spendstat.domain.account.Account;
import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.account.port.out.AccountRepository;
import com.spendstat.domain.shared.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountRepository {

    private final AccountJpaRepository jpaRepository;

    @Override
    public Account save(Account account) {
        return jpaRepository.save(AccountJpaEntity.fromDomain(account)).toDomain();
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return jpaRepository.findById(id.getValue()).map(AccountJpaEntity::toDomain);
    }

    @Override
    public List<Account> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.getValue()).stream()
                .map(AccountJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(AccountId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
