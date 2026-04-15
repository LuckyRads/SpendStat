package com.spendstat.adapter.in.web.dto.account;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountRequest(
        @NotBlank(message = "Account name is required") String name
) {
}
