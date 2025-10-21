package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.AddressDto;
import com.nhom16.VNTech.dto.UpdateProfileDto;
import com.nhom16.VNTech.entity.Address;
import com.nhom16.VNTech.entity.User;
import com.nhom16.VNTech.repository.UserRepository;
import com.nhom16.VNTech.service.EmailService;
import com.nhom16.VNTech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public Optional<User> getProfileByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateProfile(String email, UpdateProfileDto profileDto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        user.setUsername(profileDto.getUsername());
        user.setGender(profileDto.getGender());
        user.setDateOfBirth(profileDto.getDateOfBirth());
        user.setAvatar(profileDto.getAvatar());
        user.setUpdatedAt(LocalDateTime.now());

        if(!profileDto.getAddresses().isEmpty()){
            List<Address> updatedAddresses = profileDto.getAddresses().stream().map(a -> {
                Address address = new Address();
                address.setId(a.getId());
                address.setRecipientName(a.getRecipientName());
                address.setPhoneNumber(a.getPhoneNumber());
                address.setProvince(a.getProvince());
                address.setWard(a.getWard());
                address.setAddressDetail(a.getAddressDetail());
                address.setDefault(a.isDefault());
                address.setUser(user);
                address.setUpdatedAt(LocalDateTime.now());
                return address;
            }).toList();
            user.setAddress(updatedAddresses);
        }
        userRepository.save(user);
        }
}
