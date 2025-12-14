package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.ShippingDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingDistanceRepository extends JpaRepository<ShippingDistance, Long> {

    Optional<ShippingDistance> findByProvinceIgnoreCaseAndIsActiveTrue(String province);

    List<ShippingDistance> findAllByIsActiveTrueOrderByDistanceKm();

    boolean existsByProvinceIgnoreCase(String province);
}
