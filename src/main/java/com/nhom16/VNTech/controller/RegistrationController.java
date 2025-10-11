package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.entity.User;
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
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user, Model model) {
        userService.register(user);
        model.addAttribute("email", user.getEmail());
        return "verify-email";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        boolean verified = userService.verifyEmail(token);
        model.addAttribute("verified", verified);
        return "verified";
    }
}
