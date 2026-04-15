package com.spendstat.adapter.out.persistence.transaction;

import com.spendstat.domain.account.AccountId;
import com.spendstat.domain.category.CategoryId;
import com.spendstat.domain.shared.CurrencyCode;
import com.spendstat.domain.shared.Money;
import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.transaction.Transaction;
import com.spendstat.domain.transaction.TransactionId;
import com.spendstat.domain.transaction.TransactionSource;
import com.spendstat.domain.transaction.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column
    private UUID accountId;

    @Column
    private UUID categoryId;

    @Column
    private String externalId;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column
    private String description;

    @Column
    private String merchant;

    @Column(nullable = false)
    private LocalDate txDate;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private Instant createdAt;

    public static TransactionJpaEntity fromDomain(Transaction tx) {
        TransactionJpaEntity e = new TransactionJpaEntity();
        e.id = tx.getId().getValue();
        e.userId = tx.getUserId().getValue();
        e.accountId = tx.getAccountIdValue();
        e.categoryId = tx.getCategoryIdValue();
        e.externalId = tx.getExternalId();
        e.amount = tx.getAmount().getAmount();
        e.currency = tx.getAmount().getCurrency().getValue();
        e.description = tx.getDescription();
        e.merchant = tx.getMerchant();
        e.txDate = tx.getTxDate();
        e.type = tx.getType().name();
        e.source = tx.getSource().name();
        e.createdAt = tx.getCreatedAt();
        return e;
    }

    public Transaction toDomain() {
        AccountId domainAccountId = accountId != null ? AccountId.of(accountId) : null;
        CategoryId domainCategoryId = categoryId != null ? CategoryId.of(categoryId) : null;
        Money money = Money.of(amount, CurrencyCode.of(currency));
        return Transaction.from(
                TransactionId.of(id), UserId.of(userId), domainAccountId, domainCategoryId,
                money, description, merchant, txDate,
                TransactionType.valueOf(type), TransactionSource.valueOf(source), externalId, createdAt
        );
    }
}
