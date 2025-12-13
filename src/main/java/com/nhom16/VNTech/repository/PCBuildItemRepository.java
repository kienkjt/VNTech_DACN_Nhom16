package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.PCBuildItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PCBuildItemRepository extends JpaRepository<PCBuildItem, Long> {
    List<PCBuildItem> findByPcBuildId(Long pcBuildId);

    Optional<PCBuildItem> findByPcBuildIdAndComponentType(Long pcBuildId, String componentType);
}
