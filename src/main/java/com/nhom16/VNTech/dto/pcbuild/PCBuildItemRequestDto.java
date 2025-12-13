package com.nhom16.VNTech.dto.pcbuild;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PCBuildItemRequestDto {
    private Long productId;
    private String componentType;
    private int quantity = 1;
}
