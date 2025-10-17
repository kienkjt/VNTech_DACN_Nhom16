package com.nhom16.VNTech.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String status;
    private int totalPrice;
    private LocalDateTime craeteAt;

    @ManyToOne()
    private Address address;

    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Payment payment;
}
