package com.nhom16.VNTech.config;

import com.nhom16.VNTech.entity.ShippingDistance;
import com.nhom16.VNTech.repository.ShippingDistanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class ShippingDataInitializer implements CommandLineRunner {

    private final ShippingDistanceRepository shippingDistanceRepository;

    @Override
    public void run(String... args) {

        if (shippingDistanceRepository.count() > 0) {
            return;
        }

        System.out.println("Initializing shipping distance data (pricing by distance)...");

        List<ShippingDistance> distances = new ArrayList<>();

        // ===== HÀ NỘI =====
        distances.add(create("Hà Nội", 0));

        // ===== LÂN CẬN =====
        distances.add(create("Bắc Ninh", 30));
        distances.add(create("Hưng Yên", 50));
        distances.add(create("Vĩnh Phúc", 55));
        distances.add(create("Bắc Giang", 50));
        distances.add(create("Hà Nam", 60));

        // ===== MIỀN BẮC =====
        distances.add(create("Hải Dương", 60));
        distances.add(create("Nam Định", 90));
        distances.add(create("Ninh Bình", 95));
        distances.add(create("Thái Bình", 110));
        distances.add(create("Hải Phòng", 120));
        distances.add(create("Quảng Ninh", 150));
        distances.add(create("Thái Nguyên", 80));
        distances.add(create("Phú Thọ", 85));
        distances.add(create("Hòa Bình", 75));
        distances.add(create("Yên Bái", 180));
        distances.add(create("Tuyên Quang", 165));
        distances.add(create("Bắc Kạn", 165));
        distances.add(create("Lạng Sơn", 155));
        distances.add(create("Cao Bằng", 280));
        distances.add(create("Sơn La", 310));
        distances.add(create("Lào Cai", 320));
        distances.add(create("Hà Giang", 330));
        distances.add(create("Lai Châu", 450));
        distances.add(create("Điện Biên", 470));

        // ===== BẮC TRUNG BỘ =====
        distances.add(create("Thanh Hóa", 150));
        distances.add(create("Nghệ An", 290));
        distances.add(create("Hà Tĩnh", 340));
        distances.add(create("Quảng Bình", 490));
        distances.add(create("Quảng Trị", 610));
        distances.add(create("Thừa Thiên Huế", 660));

        // ===== MIỀN TRUNG =====
        distances.add(create("Đà Nẵng", 760));
        distances.add(create("Quảng Nam", 850));
        distances.add(create("Quảng Ngãi", 880));
        distances.add(create("Bình Định", 1040));
        distances.add(create("Phú Yên", 1170));
        distances.add(create("Khánh Hòa", 1280));
        distances.add(create("Ninh Thuận", 1420));
        distances.add(create("Bình Thuận", 1560));
        distances.add(create("Kon Tum", 1050));
        distances.add(create("Gia Lai", 1050));
        distances.add(create("Đắk Lắk", 1350));
        distances.add(create("Đắk Nông", 1450));
        distances.add(create("Lâm Đồng", 1480));

        // ===== ĐÔNG NAM BỘ =====
        distances.add(create("TP. Hồ Chí Minh", 1700));
        distances.add(create("Bình Dương", 1680));
        distances.add(create("Đồng Nai", 1720));
        distances.add(create("Bà Rịa - Vũng Tàu", 1780));
        distances.add(create("Tây Ninh", 1850));
        distances.add(create("Bình Phước", 1750));

        // ===== ĐBSCL =====
        distances.add(create("Long An", 1750));
        distances.add(create("Tiền Giang", 1730));
        distances.add(create("Bến Tre", 1800));
        distances.add(create("Trà Vinh", 1850));
        distances.add(create("Vĩnh Long", 1800));
        distances.add(create("Đồng Tháp", 1780));
        distances.add(create("An Giang", 1900));
        distances.add(create("Kiên Giang", 1950));
        distances.add(create("Cần Thơ", 1850));
        distances.add(create("Hậu Giang", 1880));
        distances.add(create("Sóc Trăng", 1920));
        distances.add(create("Bạc Liêu", 1980));
        distances.add(create("Cà Mau", 2050));

        shippingDistanceRepository.saveAll(distances);

    }

    private ShippingDistance create(String province, int distanceKm) {

        int fee;
        int days;

        if (distanceKm < 50) {
            fee = 25000;
            days = 1;
        } else if (distanceKm <= 150) {
            fee = 30000;
            days = 2;
        } else if (distanceKm <= 400) {
            fee = 45000;
            days = 3;
        } else if (distanceKm <= 800) {
            fee = 55000;
            days = 4;
        } else {
            fee = 65000;
            days = 6;
        }

        ShippingDistance sd = new ShippingDistance();
        sd.setProvince(province);
        sd.setDistanceKm(distanceKm);
        sd.setBaseFee(fee);
        sd.setEstimatedDays(days);
        sd.setIsActive(true);

        return sd;
    }
}
