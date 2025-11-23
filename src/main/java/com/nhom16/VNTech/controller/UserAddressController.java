package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.AddressDto;
import com.nhom16.VNTech.dto.AddressRequestDto;
import com.nhom16.VNTech.security.JwtUtil;
import com.nhom16.VNTech.service.AddressService;
import com.nhom16.VNTech.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addresses")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UserAddressController {

    private final AddressService addressService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserAddressController(AddressService addressService, UserService userService, JwtUtil jwtUtil) {
        this.addressService = addressService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                return userService.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"))
                        .getId();
            }
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn!");
        }
        throw new RuntimeException("Không tìm thấy JWT trong header Authorization!");
    }

    @GetMapping("")
    public ResponseEntity<APIResponse<List<AddressDto>>> getUserAddresses(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        List<AddressDto> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(APIResponse.success(addresses, "Lấy danh sách địa chỉ thành công"));
    }

    @PostMapping("")
    public ResponseEntity<APIResponse<AddressDto>> addAddress(
            HttpServletRequest request,
            @Valid @RequestBody AddressRequestDto addressDto) {
        Long userId = extractUserIdFromRequest(request);
        AddressDto newAddress = addressService.addAddress(userId, addressDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(newAddress, "Thêm địa chỉ thành công"));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<APIResponse<AddressDto>> updateAddress(
            HttpServletRequest request,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto addressDto) {
        Long userId = extractUserIdFromRequest(request);
        AddressDto updatedAddress = addressService.updateAddress(userId, addressId, addressDto);
        return ResponseEntity.ok(APIResponse.success(updatedAddress, "Cập nhật địa chỉ thành công"));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<APIResponse<Void>> deleteAddress(
            HttpServletRequest request,
            @PathVariable Long addressId) {
        Long userId = extractUserIdFromRequest(request);
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(APIResponse.success(null, "Xóa địa chỉ thành công"));
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<APIResponse<AddressDto>> setDefaultAddress(
            HttpServletRequest request,
            @PathVariable Long addressId) {
        Long userId = extractUserIdFromRequest(request);
        AddressDto defaultAddress = addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(APIResponse.success(defaultAddress, "Đặt địa chỉ mặc định thành công"));
    }

    @GetMapping("/default")
    public ResponseEntity<APIResponse<AddressDto>> getDefaultAddress(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        AddressDto defaultAddress = addressService.getDefaultAddress(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ mặc định"));
        return ResponseEntity.ok(APIResponse.success(defaultAddress, "Lấy địa chỉ mặc định thành công"));
    }
}