package com.nhom16.VNTech.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Dùng để truyền dữ liệu hình ảnh sản phẩm
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDto {
    private String imageUrl;
    private boolean isMain;
}
