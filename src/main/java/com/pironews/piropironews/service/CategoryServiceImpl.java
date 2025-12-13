package com.pironews.piropironews.service;


import com.pironews.piropironews.model.Category;
import com.pironews.piropironews.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.webjars.NotFoundException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl {

    private final CategoryRepository categoryRepository;

    public List<Category> fetchAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Category fetchCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found: " + categoryId));
    }
}

