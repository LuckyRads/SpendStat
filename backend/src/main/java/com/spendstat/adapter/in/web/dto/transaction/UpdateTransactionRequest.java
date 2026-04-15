package com.spendstat.adapter.in.web.dto.transaction;

import java.util.UUID;

public record UpdateTransactionRequest(
        UUID categoryId,   // nullable
        String description,
        String merchant
) {
}
