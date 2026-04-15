package com.spendstat.domain.account.port.in;

import com.spendstat.domain.account.Account;
import com.spendstat.domain.shared.UserId;

import java.math.BigDecimal;

public interface CreateAccountUseCase {

    record CreateAccountCommand(UserId userId, String name, String currency, BigDecimal initialBalance) {
    }

    Account createAccount(CreateAccountCommand command);
}
