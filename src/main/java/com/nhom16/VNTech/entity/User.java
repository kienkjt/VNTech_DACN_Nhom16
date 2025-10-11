package com.nhom16.VNTech.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;

    @Column(nullable = false, unique = true)
    private String email;
    private String phone;
    private String password;
    private String role;
    private String createdAt;
    private Boolean isActive;
    @Column(nullable = false)
    private boolean enabled;

    private String otpCode;
    private LocalDateTime otpExpiry;

}
