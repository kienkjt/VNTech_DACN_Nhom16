package com.nhom16.VNTech.dto;

import lombok.Data;

@Data
public class AddressDto {
    private Long id;
    private String recipientName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String addressDetail;
    private boolean isDefault;
}
