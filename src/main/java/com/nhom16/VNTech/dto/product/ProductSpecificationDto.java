package com.nhom16.VNTech.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Dùng để truyền thông tin cấu hình sản phẩm (specification) giữa các lớp trong ứng dụng
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecificationDto {
    private String keyName;
    private String value;
}
