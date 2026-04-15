package com.spendstat.domain.statistics.port.in;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.statistics.CategoryTotal;
import com.spendstat.domain.statistics.DailyBalance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GetStatisticsUseCase {

    record SummaryResult(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netBalance) {
    }

    SummaryResult getSummary(UserId userId, LocalDate from, LocalDate to);

    List<CategoryTotal> getCategoryBreakdown(UserId userId, LocalDate from, LocalDate to);

    List<DailyBalance> getBalanceHistory(UserId userId, UUID accountId, LocalDate from, LocalDate to);
}
