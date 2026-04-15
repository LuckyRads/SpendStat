package com.spendstat.domain.category.port.in;

import com.spendstat.domain.category.Category;
import com.spendstat.domain.category.CategoryId;
import com.spendstat.domain.shared.UserId;

import java.util.List;

public interface QueryCategoriesUseCase {

    List<Category> getCategoriesForUser(UserId userId);

    Category getCategoryById(CategoryId id, UserId userId);
}
