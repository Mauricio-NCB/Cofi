package com.website.main.service;
import java.util.List;
import org.springframework.stereotype.Service;

import com.website.main.dto.Category.CategoryResponseDTO;
import com.website.main.mapper.CategoryMapper;
import com.website.main.model.Category;
import com.website.main.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryResponseDTO> findAll() {
        List<Category> categories = categoryRepository.findAll();
        
        return categories.stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    public List<CategoryResponseDTO> findAllById(List<Integer> ids) {
        List<Category> categories = categoryRepository.findAllById(ids);

        return categories.stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    public List<CategoryResponseDTO> findCategoriesForUser(Integer userId) {
        List<Category> categories = categoryRepository.findByUsers_id(userId);

        return categories.stream()
                .map(categoryMapper::toDTO)
                .toList();
    }
}
