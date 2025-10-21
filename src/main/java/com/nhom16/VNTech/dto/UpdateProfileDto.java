package com.nhom16.VNTech.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UpdateProfileDto {
    private String username;
    private String fullName;
    private String gender;
    private  String avatar;
    private Date dateOfBirth;
    private List<AddressDto> addresses;
}
