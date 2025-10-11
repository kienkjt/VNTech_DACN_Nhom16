package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.service.UserService;
import com.nhom16.VNTech.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegistrationDto userDto, Model model) {
        userService.registerNewUserAccount(userDto);
        model.addAttribute("email", userDto.getEmail());
        return "verify-otp";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtpPage() {
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp, Model model) {
        boolean valid = verificationTokenService.validateVerificationToken(otp);
        if (valid) {
            model.addAttribute("message", "Tài khoản của bạn đã được kích hoạt thành công!");
            return "verified";
        } else {
            model.addAttribute("message", "Mã OTP không hợp lệ hoặc đã hết hạn!");
            return "verify-otp";
        }
    }
}
