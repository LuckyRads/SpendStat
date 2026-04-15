package com.spendstat.domain.category;

import com.spendstat.domain.shared.UserId;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Category {

    private final CategoryId id;
    private final UserId userId; // null for system default categories
    private String name;
    private String color;
    private String icon;
    private final boolean isDefault;
    private final Instant createdAt;

    private Category(CategoryId id, UserId userId, String name, String color, String icon,
                     boolean isDefault, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    public static Category from(CategoryId id, UserId userId, String name, String color, String icon,
                                boolean isDefault, Instant createdAt) {
        return new Category(id, userId, name, color, icon, isDefault, createdAt);
    }

    public boolean isVisibleTo(UserId requestingUserId) {
        return isDefault || (userId != null && userId.equals(requestingUserId));
    }
}
