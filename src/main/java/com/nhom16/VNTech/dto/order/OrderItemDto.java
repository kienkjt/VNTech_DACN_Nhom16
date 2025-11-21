package com.nhom16.VNTech.dto.order;

import com.nhom16.VNTech.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long id;
    private int price;
    private int quantity;
    private int totalPrice;
    private ProductDto product;
}
