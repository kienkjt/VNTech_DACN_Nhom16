package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.user.UserDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AdminUserService {
    @Transactional
    void  deleteUser(Long id);
    List<UserDto> getAllUserDtos();
    UserDto getUserDtoById(Long id);
}
