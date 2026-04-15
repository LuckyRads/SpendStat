package com.spendstat.domain.statistics.port.out;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.statistics.CategoryTotal;
import com.spendstat.domain.statistics.DailyBalance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StatisticsQueryPort {

    BigDecimal getTotalIncome(UserId userId, LocalDate from, LocalDate to);

    BigDecimal getTotalExpenses(UserId userId, LocalDate from, LocalDate to);

    List<CategoryTotal> getCategoryBreakdown(UserId userId, LocalDate from, LocalDate to);

    List<DailyBalance> getDailyBalanceHistory(UserId userId, UUID accountId, LocalDate from, LocalDate to);
}
