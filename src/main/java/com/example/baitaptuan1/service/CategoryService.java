package com.example.baitaptuan1.service;

import com.example.baitaptuan1.entity.Category;
import com.example.baitaptuan1.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryByid(Long id){
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category saveCategory(Category category){
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id){
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy thể loại cần xóa.");
        }
        categoryRepository.deleteById(id);
    }
}
