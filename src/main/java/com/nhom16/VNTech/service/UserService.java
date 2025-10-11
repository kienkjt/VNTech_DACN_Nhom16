package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenService tokenService;

    @Autowired
    private EmailService emailService;

    public User registerNewUserAccount(UserRegistrationDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
        user.setEnabled(false);
        userRepository.save(user);

        //Tạo mã OTP và gửi email
        String otp = tokenService.createVerificationToken(user);

        String subject = "VNTech - Xác thực tài khoản của bạn";
        String message = "Xin chào!\n\nMã xác thực của bạn là: " + otp +
                "\nMã có hiệu lực trong 5 phút.\n\nCảm ơn bạn đã đăng ký tại VNTech.";
        emailService.sendEmail(user.getEmail(), subject, message);

        return user;
    }
}
