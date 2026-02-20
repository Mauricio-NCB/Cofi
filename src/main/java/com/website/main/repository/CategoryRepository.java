package com.website.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.website.main.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}