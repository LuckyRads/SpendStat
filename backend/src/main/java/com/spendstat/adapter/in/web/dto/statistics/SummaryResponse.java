package com.spendstat.adapter.in.web.dto.statistics;

import com.spendstat.domain.statistics.port.in.GetStatisticsUseCase;

import java.math.BigDecimal;

public record SummaryResponse(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netBalance) {

    public static SummaryResponse from(GetStatisticsUseCase.SummaryResult result) {
        return new SummaryResponse(result.totalIncome(), result.totalExpenses(), result.netBalance());
    }
}
