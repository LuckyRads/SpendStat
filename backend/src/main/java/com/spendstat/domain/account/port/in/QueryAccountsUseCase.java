package com.spendstat.domain.account.port.in;

import com.spendstat.domain.account.Account;
import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.shared.UserId;

import java.util.List;

public interface QueryAccountsUseCase {

    List<Account> getAccountsByUser(UserId userId);

    Account getAccountById(AccountId accountId, UserId userId);
}
