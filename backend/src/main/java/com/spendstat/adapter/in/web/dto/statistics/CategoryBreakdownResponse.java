package com.spendstat.adapter.in.web.dto.statistics;

import com.spendstat.domain.statistics.CategoryTotal;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CategoryBreakdownResponse(List<Item> items) {

    public record Item(UUID categoryId, String categoryName, BigDecimal total) {
        public static Item from(CategoryTotal ct) {
            return new Item(ct.categoryId(), ct.categoryName(), ct.total());
        }
    }

    public static CategoryBreakdownResponse from(List<CategoryTotal> totals) {
        return new CategoryBreakdownResponse(totals.stream().map(Item::from).toList());
    }
}
