package com.spendstat.application.statistics;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.statistics.CategoryTotal;
import com.spendstat.domain.statistics.DailyBalance;
import com.spendstat.domain.statistics.port.in.GetStatisticsUseCase;
import com.spendstat.domain.statistics.port.out.StatisticsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatisticsService implements GetStatisticsUseCase {

    private final StatisticsQueryPort statisticsQueryPort;

    @Override
    @Transactional(readOnly = true)
    public SummaryResult getSummary(UserId userId, LocalDate from, LocalDate to) {
        BigDecimal income = statisticsQueryPort.getTotalIncome(userId, from, to);
        BigDecimal expenses = statisticsQueryPort.getTotalExpenses(userId, from, to);
        BigDecimal net = income.add(expenses); // expenses are stored as negative
        return new SummaryResult(income, expenses.abs(), net);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTotal> getCategoryBreakdown(UserId userId, LocalDate from, LocalDate to) {
        return statisticsQueryPort.getCategoryBreakdown(userId, from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyBalance> getBalanceHistory(UserId userId, UUID accountId, LocalDate from, LocalDate to) {
        return statisticsQueryPort.getDailyBalanceHistory(userId, accountId, from, to);
    }
}
