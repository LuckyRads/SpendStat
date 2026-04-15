package com.spendstat.adapter.out.persistence.statistics;

import com.spendstat.adapter.out.persistence.account.AccountJpaRepository;
import com.spendstat.adapter.out.persistence.category.CategoryJpaRepository;
import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.statistics.CategoryTotal;
import com.spendstat.domain.statistics.DailyBalance;
import com.spendstat.domain.statistics.port.out.StatisticsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class StatisticsPersistenceAdapter implements StatisticsQueryPort {

    @PersistenceContext
    private EntityManager em;

    private final AccountJpaRepository accountJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public BigDecimal getTotalIncome(UserId userId, LocalDate from, LocalDate to) {
        BigDecimal result = em.createQuery(
                        "SELECT COALESCE(SUM(t.amount), 0) FROM TransactionJpaEntity t " +
                        "WHERE t.userId = :userId AND t.type = 'INCOME' AND t.txDate BETWEEN :from AND :to",
                        BigDecimal.class)
                .setParameter("userId", userId.getValue())
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalExpenses(UserId userId, LocalDate from, LocalDate to) {
        BigDecimal result = em.createQuery(
                        "SELECT COALESCE(SUM(t.amount), 0) FROM TransactionJpaEntity t " +
                        "WHERE t.userId = :userId AND t.type = 'EXPENSE' AND t.txDate BETWEEN :from AND :to",
                        BigDecimal.class)
                .setParameter("userId", userId.getValue())
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public List<CategoryTotal> getCategoryBreakdown(UserId userId, LocalDate from, LocalDate to) {
        List<Object[]> rows = em.createQuery(
                        "SELECT t.categoryId, SUM(t.amount) FROM TransactionJpaEntity t " +
                        "WHERE t.userId = :userId AND t.type = 'EXPENSE' AND t.txDate BETWEEN :from AND :to " +
                        "GROUP BY t.categoryId",
                        Object[].class)
                .setParameter("userId", userId.getValue())
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();

        return rows.stream().map(row -> {
            UUID categoryId = (UUID) row[0];
            BigDecimal total = (BigDecimal) row[1];
            String name = categoryId != null
                    ? categoryJpaRepository.findById(categoryId)
                            .map(c -> c.getName())
                            .orElse("Uncategorised")
                    : "Uncategorised";
            return new CategoryTotal(categoryId, name, total.abs());
        }).toList();
    }

    @Override
    public List<DailyBalance> getDailyBalanceHistory(UserId userId, UUID accountId, LocalDate from, LocalDate to) {
        // Step 1: compute opening balance (initial balances + all transactions before 'from')
        BigDecimal initialBalanceSum = getInitialBalanceSum(userId, accountId);
        BigDecimal preFromSum = getTransactionSumBefore(userId, accountId, from);
        BigDecimal openingBalance = initialBalanceSum.add(preFromSum);

        // Step 2: fetch daily net amounts within the range
        String dailyJpql = "SELECT t.txDate, SUM(t.amount) FROM TransactionJpaEntity t " +
                           "WHERE t.userId = :userId " +
                           (accountId != null ? "AND t.accountId = :accountId " : "") +
                           "AND t.txDate BETWEEN :from AND :to " +
                           "GROUP BY t.txDate ORDER BY t.txDate";
        var dailyQuery = em.createQuery(dailyJpql, Object[].class)
                .setParameter("userId", userId.getValue())
                .setParameter("from", from)
                .setParameter("to", to);
        if (accountId != null) dailyQuery.setParameter("accountId", accountId);
        List<Object[]> rows = dailyQuery.getResultList();

        // Step 3: build a map of date -> net change
        Map<LocalDate, BigDecimal> dailyMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            dailyMap.put((LocalDate) row[0], (BigDecimal) row[1]);
        }

        // Step 4: generate all days with running balance
        List<DailyBalance> result = new ArrayList<>();
        BigDecimal running = openingBalance;
        LocalDate current = from;
        while (!current.isAfter(to)) {
            running = running.add(dailyMap.getOrDefault(current, BigDecimal.ZERO));
            result.add(new DailyBalance(current, running));
            current = current.plusDays(1);
        }
        return result;
    }

    private BigDecimal getInitialBalanceSum(UserId userId, UUID accountId) {
        String jpql = accountId != null
                ? "SELECT COALESCE(SUM(a.initialBalance), 0) FROM AccountJpaEntity a WHERE a.userId = :userId AND a.id = :accountId"
                : "SELECT COALESCE(SUM(a.initialBalance), 0) FROM AccountJpaEntity a WHERE a.userId = :userId";
        var q = em.createQuery(jpql, BigDecimal.class).setParameter("userId", userId.getValue());
        if (accountId != null) q.setParameter("accountId", accountId);
        BigDecimal result = q.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    private BigDecimal getTransactionSumBefore(UserId userId, UUID accountId, LocalDate before) {
        String jpql = "SELECT COALESCE(SUM(t.amount), 0) FROM TransactionJpaEntity t " +
                      "WHERE t.userId = :userId AND t.txDate < :before" +
                      (accountId != null ? " AND t.accountId = :accountId" : "");
        var q = em.createQuery(jpql, BigDecimal.class)
                .setParameter("userId", userId.getValue())
                .setParameter("before", before);
        if (accountId != null) q.setParameter("accountId", accountId);
        BigDecimal result = q.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}
