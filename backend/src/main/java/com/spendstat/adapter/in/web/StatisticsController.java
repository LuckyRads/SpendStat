package com.spendstat.adapter.in.web;

import com.spendstat.adapter.in.web.dto.statistics.BalanceHistoryResponse;
import com.spendstat.adapter.in.web.dto.statistics.CategoryBreakdownResponse;
import com.spendstat.adapter.in.web.dto.statistics.SummaryResponse;
import com.spendstat.domain.statistics.port.in.GetStatisticsUseCase;
import com.spendstat.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final GetStatisticsUseCase getStatisticsUseCase;

    @GetMapping("/summary")
    public SummaryResponse summary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return SummaryResponse.from(
                getStatisticsUseCase.getSummary(SecurityUtils.getCurrentUserId(), from, to)
        );
    }

    @GetMapping("/by-category")
    public CategoryBreakdownResponse byCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return CategoryBreakdownResponse.from(
                getStatisticsUseCase.getCategoryBreakdown(SecurityUtils.getCurrentUserId(), from, to)
        );
    }

    @GetMapping("/balance-history")
    public BalanceHistoryResponse balanceHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID accountId
    ) {
        return BalanceHistoryResponse.from(
                getStatisticsUseCase.getBalanceHistory(SecurityUtils.getCurrentUserId(), accountId, from, to)
        );
    }
}
