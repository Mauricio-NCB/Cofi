package com.website.main.mapper;

import com.website.main.model.Category;

import org.springframework.stereotype.Component;

import com.website.main.dto.Category.CategoryResponseDTO;

@Component
public class CategoryMapper {

    public CategoryResponseDTO toDTO(Category category) {

        if (category == null) return null;

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        
        return dto;
    }
}
