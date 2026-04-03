package com.website.main.controller;

import org.springframework.web.bind.annotation.RestController;

import com.website.main.dto.Category.CategoryResponseDTO;
import com.website.main.service.CategoryService;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @GetMapping()    
    public List<CategoryResponseDTO> getAllCategories() {
        // Aquí iría la lógica para obtener todas las categorías
        return categoryService.findAll();
    }

    @GetMapping("/user")
    public List<CategoryResponseDTO> getCategoriesForUser() {
        // Aquí iría la lógica para obtener las categorías para el usuario
        Integer userId = (Integer) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        return categoryService.findCategoriesForUser(userId);
    }
}
