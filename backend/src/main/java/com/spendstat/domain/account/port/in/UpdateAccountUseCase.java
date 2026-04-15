package com.spendstat.domain.account.port.in;

import com.spendstat.domain.account.Account;
import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.shared.UserId;

public interface UpdateAccountUseCase {

    record UpdateAccountCommand(AccountId accountId, UserId userId, String name) {
    }

    Account updateAccount(UpdateAccountCommand command);
}
