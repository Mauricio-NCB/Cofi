package com.website.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.website.main.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
}