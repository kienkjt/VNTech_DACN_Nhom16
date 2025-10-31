package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.CategoryRequestDto;
import com.nhom16.VNTech.dto.CategoryResponseDto;
import com.nhom16.VNTech.entity.Category;
import com.nhom16.VNTech.repository.CategoryRepository;
import com.nhom16.VNTech.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        Category category = new Category();
        category.setName(dto.getCategoryName());
        category.setCreatedAt(LocalDateTime.now());
        categoryRepository.save(category);
        return mapToDto(category);
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(dto.getCategoryName());
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
        return mapToDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapToDto(category);
    }

    private CategoryResponseDto mapToDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setCategoryName(category.getName());
        dto.setCreatedDate(category.getCreatedAt());
        dto.setUpdatedDate(category.getUpdatedAt());
        return dto;
    }
}
