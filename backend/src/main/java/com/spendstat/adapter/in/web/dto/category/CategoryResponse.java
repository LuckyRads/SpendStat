package com.spendstat.adapter.in.web.dto.category;

import com.spendstat.domain.category.Category;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String color,
        String icon,
        boolean isDefault
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId().getValue(),
                category.getName(),
                category.getColor(),
                category.getIcon(),
                category.isDefault()
        );
    }
}
