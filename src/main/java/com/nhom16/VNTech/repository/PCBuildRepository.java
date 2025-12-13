package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.PCBuild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PCBuildRepository extends JpaRepository<PCBuild, Long> {
    List<PCBuild> findByUserId(Long userId);

    Optional<PCBuild> findByUserIdAndId(Long userId, Long id);
}
