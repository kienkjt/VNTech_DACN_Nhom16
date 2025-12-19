package com.nhom16.VNTech.repository;

import com.nhom16.VNTech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);

    // Số người dùng mới theo tháng trong năm
    @Query("SELECT FUNCTION('MONTH', u.createdAt), COUNT(u) FROM User u WHERE FUNCTION('YEAR', u.createdAt) = :year GROUP BY FUNCTION('MONTH', u.createdAt) ORDER BY FUNCTION('MONTH', u.createdAt)")
    List<Object[]> countNewUsersPerMonth(@Param("year") int year);
}