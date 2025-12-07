package com.nhom16.VNTech.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderDto {
    private Long id;
//    private String username;
    private String email;
    private String fullName;
    private String phone;
}
