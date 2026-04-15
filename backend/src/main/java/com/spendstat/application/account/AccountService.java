package com.spendstat.application.account;

import com.spendstat.application.exception.ResourceNotFoundException;
import com.spendstat.domain.account.Account;
import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.account.port.in.CreateAccountUseCase;
import com.spendstat.domain.account.port.in.DeleteAccountUseCase;
import com.spendstat.domain.account.port.in.QueryAccountsUseCase;
import com.spendstat.domain.account.port.in.UpdateAccountUseCase;
import com.spendstat.domain.account.port.out.AccountRepository;
import com.spendstat.domain.shared.Money;
import com.spendstat.domain.shared.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService implements CreateAccountUseCase, UpdateAccountUseCase,
        DeleteAccountUseCase, QueryAccountsUseCase {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Account createAccount(CreateAccountCommand command) {
        Money initialBalance = Money.of(command.initialBalance(), command.currency());
        Account account = Account.init(command.userId(), command.name(), initialBalance);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account updateAccount(UpdateAccountCommand command) {
        Account account = requireOwned(command.accountId(), command.userId());
        account.rename(command.name());
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deleteAccount(AccountId accountId, UserId userId) {
        requireOwned(accountId, userId);
        accountRepository.deleteById(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAccountsByUser(UserId userId) {
        return accountRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountById(AccountId accountId, UserId userId) {
        return requireOwned(accountId, userId);
    }

    private Account requireOwned(AccountId accountId, UserId userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId.getValue()));
        if (!account.isOwnedBy(userId)) {
            throw new ResourceNotFoundException("Account", accountId.getValue());
        }
        return account;
    }
}
