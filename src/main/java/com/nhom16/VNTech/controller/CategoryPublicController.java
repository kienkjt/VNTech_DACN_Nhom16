package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.CategoryResponseDto;
import com.nhom16.VNTech.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryPublicController {

    private final CategoryService categoryService;

    public CategoryPublicController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public ResponseEntity<APIResponse<List<CategoryResponseDto>>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(APIResponse.success(categories, "Lấy danh sách danh mục thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<CategoryResponseDto>> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(APIResponse.success(category, "Lấy thông tin danh mục thành công"));
    }
}
