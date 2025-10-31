package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.CategoryDto;
import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);
    CategoryDto updateCategory(Long id, CategoryDto dto);
    void deleteCategory(Long id);
    List<CategoryDto> getAllCategories();
    CategoryDto getCategoryById(Long id);
}
