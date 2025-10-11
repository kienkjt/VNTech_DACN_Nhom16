package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.UserRegistrationDto;
import com.nhom16.VNTech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

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

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
        boolean isValid = userService.verifyOtp(email, otp);
        if (isValid) {
            model.addAttribute("message", "Tài khoản của bạn đã được xác minh thành công!");
            return "verified";
        } else {
            model.addAttribute("error", "Mã OTP không hợp lệ hoặc đã hết hạn.");
            model.addAttribute("email", email);
            return "verify-otp";
        }
    }
}
