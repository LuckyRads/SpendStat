package com.spendstat.application.category;

import com.spendstat.application.exception.ResourceNotFoundException;
import com.spendstat.domain.category.Category;
import com.spendstat.domain.category.CategoryId;
import com.spendstat.domain.category.port.in.QueryCategoriesUseCase;
import com.spendstat.domain.category.port.out.CategoryRepository;
import com.spendstat.domain.shared.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements QueryCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesForUser(UserId userId) {
        return categoryRepository.findAllVisibleToUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(CategoryId id, UserId userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id.getValue()));
        if (!category.isVisibleTo(userId)) {
            throw new ResourceNotFoundException("Category", id.getValue());
        }
        return category;
    }
}
