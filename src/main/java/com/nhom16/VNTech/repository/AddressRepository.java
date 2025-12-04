package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.Address;
import com.nhom16.VNTech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    Optional<Address> findByIdAndUser(Long id, User user);
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);
}
