package com.spendstat.domain.account.port.in;

import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.shared.UserId;

public interface DeleteAccountUseCase {

    void deleteAccount(AccountId accountId, UserId userId);
}
