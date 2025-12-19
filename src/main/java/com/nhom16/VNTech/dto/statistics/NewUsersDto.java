package com.nhom16.VNTech.dto.statistics;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewUsersDto {
    private Integer month;
    private Long newUsers;

    public NewUsersDto(Integer month, Long newUsers) {
        this.month = month;
        this.newUsers = newUsers;
    }

}

