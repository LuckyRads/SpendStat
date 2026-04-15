package com.spendstat.domain.transaction;

import java.util.List;

public record TransactionPage(List<Transaction> transactions, long totalCount) {
}
