package com.nhom16.VNTech.dto.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private Long id;
    private String recipientName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String addressDetail;
    private boolean isDefault;

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    public boolean getIsDefault() {
        return this.isDefault;
    }
}
