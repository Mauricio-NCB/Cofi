package com.website.main.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.website.main.model.Category;
import com.website.main.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findAllById(List<Integer> ids) {
        return categoryRepository.findAllById(ids);
    }
}
