package com.nhom16.VNTech.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientName;
    private String phoneNumber;
    private String province;       // Tỉnh/Thành phố
    private String ward;           // Phường/Xã
    private String addressDetail;  // Số nhà, tên đường

    private boolean isDefault = false;  // Địa chỉ mặc định

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

     @OneToMany(mappedBy = "address")
     private List<Order> orders;
}
