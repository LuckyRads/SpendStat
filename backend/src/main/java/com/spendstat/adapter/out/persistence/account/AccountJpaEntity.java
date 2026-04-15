package com.spendstat.adapter.out.persistence.account;

import com.spendstat.domain.account.Account;
import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.shared.CurrencyCode;
import com.spendstat.domain.shared.Money;
import com.spendstat.domain.shared.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal initialBalance;

    @Column(nullable = false)
    private Instant createdAt;

    public static AccountJpaEntity fromDomain(Account account) {
        AccountJpaEntity e = new AccountJpaEntity();
        e.id = account.getId().getValue();
        e.userId = account.getUserId().getValue();
        e.name = account.getName();
        e.currency = account.getInitialBalance().getCurrency().getValue();
        e.initialBalance = account.getInitialBalance().getAmount();
        e.createdAt = account.getCreatedAt();
        return e;
    }

    public Account toDomain() {
        Money balance = Money.of(initialBalance, CurrencyCode.of(currency));
        return Account.from(AccountId.of(id), UserId.of(userId), name, balance, createdAt);
    }
}
