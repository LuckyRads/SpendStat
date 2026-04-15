package com.spendstat.adapter.in.web;

import com.spendstat.adapter.in.web.dto.account.AccountResponse;
import com.spendstat.adapter.in.web.dto.account.CreateAccountRequest;
import com.spendstat.adapter.in.web.dto.account.UpdateAccountRequest;
import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.account.port.in.CreateAccountUseCase;
import com.spendstat.domain.account.port.in.DeleteAccountUseCase;
import com.spendstat.domain.account.port.in.QueryAccountsUseCase;
import com.spendstat.domain.account.port.in.UpdateAccountUseCase;
import com.spendstat.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final QueryAccountsUseCase queryAccountsUseCase;

    @GetMapping
    public List<AccountResponse> list() {
        return queryAccountsUseCase.getAccountsByUser(SecurityUtils.getCurrentUserId())
                .stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable UUID id) {
        return AccountResponse.from(
                queryAccountsUseCase.getAccountById(AccountId.of(id), SecurityUtils.getCurrentUserId())
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return AccountResponse.from(createAccountUseCase.createAccount(
                new CreateAccountUseCase.CreateAccountCommand(
                        SecurityUtils.getCurrentUserId(),
                        request.name(),
                        request.currency(),
                        request.initialBalance()
                )
        ));
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateAccountRequest request) {
        return AccountResponse.from(updateAccountUseCase.updateAccount(
                new UpdateAccountUseCase.UpdateAccountCommand(
                        AccountId.of(id), SecurityUtils.getCurrentUserId(), request.name()
                )
        ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        deleteAccountUseCase.deleteAccount(AccountId.of(id), SecurityUtils.getCurrentUserId());
    }
}
