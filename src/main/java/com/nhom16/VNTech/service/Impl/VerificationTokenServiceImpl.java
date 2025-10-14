package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.entity.VerificationToken;
import com.nhom16.VNTech.repository.VerificationTokenRepository;
import com.nhom16.VNTech.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    // Tạo OTP mới
    @Override
    public String createVerificationToken(User user) {
        // Xóa token cũ của user nếu có
        tokenRepository.deleteByUserId(user.getId());

        // Sinh mã OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(2);

        VerificationToken token = new VerificationToken(otp, user, expiryDate);
        tokenRepository.save(token);

        return otp;
    }

    // Xác thực mã OTP
    @Override
    public boolean validateVerificationToken(String otp) {
        Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(otp);
        if (tokenOpt.isEmpty()) return false;

        VerificationToken token = tokenOpt.get();

        // Hết hạn
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            return false;
        }

        // Kích hoạt user
        User user = token.getUser();
        user.setActive(true);

        tokenRepository.delete(token);
        return true;
    }
}
