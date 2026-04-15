package com.spendstat.adapter.out.persistence.category;

import com.spendstat.domain.category.Category;
import com.spendstat.domain.category.CategoryId;
import com.spendstat.domain.category.port.out.CategoryRepository;
import com.spendstat.domain.shared.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaRepository.findById(id.getValue()).map(CategoryJpaEntity::toDomain);
    }

    @Override
    public List<Category> findAllVisibleToUser(UserId userId) {
        return jpaRepository.findAllVisibleToUser(userId.getValue()).stream()
                .map(CategoryJpaEntity::toDomain)
                .toList();
    }
}
