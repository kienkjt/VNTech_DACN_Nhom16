package com.nhom16.VNTech.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserProfileDto {
    private String email;
    private String username;
    private String fullName;
    private String gender;
    private  String avatar;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date dateOfBirth;
    //private List<AddressDto> addresses;
}
