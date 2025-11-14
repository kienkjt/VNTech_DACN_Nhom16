package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.category.CategoryRequestDto;
import com.nhom16.VNTech.dto.category.CategoryResponseDto;
import com.nhom16.VNTech.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("")
    public ResponseEntity<APIResponse<CategoryResponseDto>> createCategory(@RequestBody CategoryRequestDto dto) {
        try {
            CategoryResponseDto created = categoryService.createCategory(dto);
            return ResponseEntity.ok(APIResponse.success(created, "Tạo danh mục thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.error("Lỗi khi tạo danh mục: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<CategoryResponseDto>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequestDto dto) {
        try {
            CategoryResponseDto updated = categoryService.updateCategory(id, dto);
            return ResponseEntity.ok(APIResponse.success(updated, "Cập nhật danh mục thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error("Không tìm thấy danh mục có ID: " + id));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(APIResponse.success(null, "Xóa danh mục thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error("Xóa thất bại: Không tìm thấy danh mục có ID: " + id));
        }
    }
}
