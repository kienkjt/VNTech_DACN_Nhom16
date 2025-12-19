package com.nhom16.VNTech.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderStatusCountDto {
    private String status;
    private Long count;

    public OrderStatusCountDto(Object status, Long count) {
        this.status = status != null ? status.toString() : null;
        this.count = count;
    }

}

