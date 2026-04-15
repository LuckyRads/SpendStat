package com.spendstat.domain.account.port.out;

import com.spendstat.domain.account.Account;
import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.shared.UserId;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(AccountId id);

    List<Account> findByUserId(UserId userId);

    void deleteById(AccountId id);
}
