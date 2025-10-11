package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Đăng ký user mới
    public User registerNewUserAccount(UserRegistrationDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
        user.setEnabled(false);

        // Tạo mã OTP
        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(2)); // Hết hạn sau 5 phút
        userRepository.save(user);

        // Gửi email chứa OTP
        String subject = "VNTech - Xác thực tài khoản của bạn";
        String message = "Xin chào!\n\nMã xác thực của bạn là: " + otp +
                "\nMã có hiệu lực trong 2 phút.\n\nCảm ơn bạn đã đăng ký tại VNTech.";
        emailService.sendEmail(user.getEmail(), subject, message);

        return user;
    }

    // Xác minh OTP
    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;

        // Kiểm tra mã OTP và thời hạn
        if (user.getOtpCode().equals(otp) && user.getOtpExpiry().isAfter(LocalDateTime.now())) {
            user.setEnabled(true);
            user.setOtpCode(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6 chữ số
        return String.valueOf(otp);
    }
}
