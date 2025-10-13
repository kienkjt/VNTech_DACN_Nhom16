package com.nhom16.VNTech.service;

import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.entity.VerificationToken;
import com.nhom16.VNTech.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    // Tạo OTP mới
    public String createVerificationToken(User user) {
        tokenRepository.deleteByUserId(user.getId());

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(2);

        VerificationToken token = new VerificationToken(otp, user, expiryDate);
        tokenRepository.save(token);

        return otp;
    }

    // Kiểm tra OTP hợp lệ
    public boolean validateVerificationToken(String otp) {
        Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(otp);

        if (tokenOpt.isEmpty()) return false;

        VerificationToken token = tokenOpt.get();
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            return false;
        }

        // Kích hoạt user
        User user = token.getUser();
        user.setEnabled(true);
        tokenRepository.delete(token);
        return true;
    }
}
