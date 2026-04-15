package com.spendstat.domain.category.port.out;

import com.spendstat.domain.category.Category;
import com.spendstat.domain.category.CategoryId;
import com.spendstat.domain.shared.UserId;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findById(CategoryId id);

    List<Category> findAllVisibleToUser(UserId userId);
}
