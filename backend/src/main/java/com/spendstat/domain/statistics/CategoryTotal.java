package com.spendstat.domain.statistics;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryTotal(UUID categoryId, String categoryName, BigDecimal total) {
}
