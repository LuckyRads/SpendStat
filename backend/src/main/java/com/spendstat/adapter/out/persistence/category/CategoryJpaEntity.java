package com.spendstat.adapter.out.persistence.category;

import com.spendstat.domain.category.Category;
import com.spendstat.domain.category.CategoryId;
import com.spendstat.domain.shared.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryJpaEntity {

    @Id
    private UUID id;

    @Column
    private UUID userId; // null for system defaults

    @Column(nullable = false)
    private String name;

    @Column
    private String color;

    @Column
    private String icon;

    @Column(nullable = false)
    private boolean isDefault;

    @Column(nullable = false)
    private Instant createdAt;

    public static CategoryJpaEntity fromDomain(Category category) {
        CategoryJpaEntity e = new CategoryJpaEntity();
        e.id = category.getId().getValue();
        e.userId = category.getUserId() != null ? category.getUserId().getValue() : null;
        e.name = category.getName();
        e.color = category.getColor();
        e.icon = category.getIcon();
        e.isDefault = category.isDefault();
        e.createdAt = category.getCreatedAt();
        return e;
    }

    public Category toDomain() {
        UserId domainUserId = userId != null ? UserId.of(userId) : null;
        return Category.from(CategoryId.of(id), domainUserId, name, color, icon, isDefault, createdAt);
    }
}
