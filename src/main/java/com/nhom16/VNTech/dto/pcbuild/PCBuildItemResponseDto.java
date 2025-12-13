package com.nhom16.VNTech.dto.pcbuild;

import com.nhom16.VNTech.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PCBuildItemResponseDto {
    private Long id;
    private String componentType;
    private int quantity;
    private Long price;
    private ProductDto product;
}
