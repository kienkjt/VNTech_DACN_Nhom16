package com.nhom16.VNTech.mapper;

import com.nhom16.VNTech.dto.category.CategoryResponseDto;
import com.nhom16.VNTech.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponseDto toCategoryResponseDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setCategoryName(category.getName());
        dto.setCreatedDate(category.getCreatedAt());
        dto.setUpdatedDate(category.getUpdatedAt());
        return dto;
    }
}