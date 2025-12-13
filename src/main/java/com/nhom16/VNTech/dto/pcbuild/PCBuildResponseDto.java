package com.nhom16.VNTech.dto.pcbuild;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PCBuildResponseDto {
    private Long id;
    private String buildName;
    private Long totalCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PCBuildItemResponseDto> items = new ArrayList<>();
}
