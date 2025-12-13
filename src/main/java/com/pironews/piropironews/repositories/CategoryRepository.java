package com.pironews.piropironews.repositories;

import com.pironews.piropironews.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    Optional<com.pironews.piropironews.model.Category> getCategoryByName(String categoryName);

    Optional<Category> findBySlug(String slug);
    boolean existsByNameIgnoreCase(String name);
}
