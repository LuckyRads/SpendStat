package com.spendstat.adapter.in.web.dto.statistics;

import com.spendstat.domain.statistics.DailyBalance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BalanceHistoryResponse(List<Point> points) {

    public record Point(LocalDate date, BigDecimal balance) {
        public static Point from(DailyBalance db) {
            return new Point(db.date(), db.balance());
        }
    }

    public static BalanceHistoryResponse from(List<DailyBalance> history) {
        return new BalanceHistoryResponse(history.stream().map(Point::from).toList());
    }
}
