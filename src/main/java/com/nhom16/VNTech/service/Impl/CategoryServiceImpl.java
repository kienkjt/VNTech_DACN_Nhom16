package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.category.CategoryRequestDto;
import com.nhom16.VNTech.dto.category.CategoryResponseDto;
import com.nhom16.VNTech.entity.Category;
import com.nhom16.VNTech.repository.CategoryRepository;
import com.nhom16.VNTech.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        if (dto.getCategoryName() == null || dto.getCategoryName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục không được để trống");
        }

        Category category = new Category();
        category.setName(dto.getCategoryName().trim());
        category.setCreatedAt(LocalDateTime.now());
        categoryRepository.save(category);

        return mapToDto(category);
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục có id: " + id));

        if (dto.getCategoryName() == null || dto.getCategoryName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục không được để trống");
        }

        category.setName(dto.getCategoryName().trim());
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);

        return mapToDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục có id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Không có danh mục nào");
        }

        return categories.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục với id: " + id));

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
