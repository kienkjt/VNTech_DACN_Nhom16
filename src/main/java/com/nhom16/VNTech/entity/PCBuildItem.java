package com.nhom16.VNTech.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "pc_build_item")
public class PCBuildItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String componentType;
    private int quantity;
    private Long price;

    @ManyToOne
    @JoinColumn(name = "pc_build_id")
    private PCBuild pcBuild;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
