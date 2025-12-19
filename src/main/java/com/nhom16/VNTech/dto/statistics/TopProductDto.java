package com.nhom16.VNTech.dto.statistics;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class TopProductDto {
    private Long productId;
    private String productName;
    private Long soldQuantity;
    private BigDecimal revenue;

    public TopProductDto(Long productId, String productName, Long soldQuantity, BigDecimal revenue) {
        this.productId = productId;
        this.productName = productName;
        this.soldQuantity = soldQuantity;
        this.revenue = revenue;
    }

}

