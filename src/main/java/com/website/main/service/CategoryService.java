package com.website.main.service;
import java.util.List;
import org.springframework.stereotype.Service;

import com.website.main.dto.Category.CategoryResponseDTO;
import com.website.main.model.Category;
import com.website.main.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponseDTO> findAll() {
        List<Category> categories = categoryRepository.findAll();
        
        return categories.stream()
                .map(category -> {
                    CategoryResponseDTO dto = new CategoryResponseDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    return dto;
                })
                .toList();
    }

    public List<CategoryResponseDTO> findAllById(List<Integer> ids) {
        List<Category> categories = categoryRepository.findAllById(ids);
        return categories.stream()
                .map(category -> {
                    CategoryResponseDTO dto = new CategoryResponseDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    return dto;
                })
                .toList();
    }
}
