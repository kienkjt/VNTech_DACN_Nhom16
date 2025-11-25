package com.nhom16.VNTech.dto.cart;

import com.nhom16.VNTech.dto.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long id;
    private int quantity;
    private double price;
    private  boolean selected;
    private ProductDto product;
}
