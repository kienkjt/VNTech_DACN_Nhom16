package com.nhom16.VNTech.dto.statistics;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class TotalNumberDto {
    private String label;
    private BigDecimal value;

    public TotalNumberDto() {
    }

    public TotalNumberDto(String label, BigDecimal value) {
        this.label = label;
        this.value = value;
    }

}

