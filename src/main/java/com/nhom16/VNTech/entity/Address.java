package com.nhom16.VNTech.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String recipientName;
    private String phoneNumber;
    private String addressDetail;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
