package com.spendstat.adapter.in.web;

import com.spendstat.adapter.in.web.dto.category.CategoryResponse;
import com.spendstat.domain.category.port.in.QueryCategoriesUseCase;
import com.spendstat.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final QueryCategoriesUseCase queryCategoriesUseCase;

    @GetMapping
    public List<CategoryResponse> list() {
        return queryCategoriesUseCase.getCategoriesForUser(SecurityUtils.getCurrentUserId())
                .stream().map(CategoryResponse::from).toList();
    }
}
