package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.cart.CartResponseDto;
import com.nhom16.VNTech.dto.pcbuild.*;
import com.nhom16.VNTech.security.JwtUtil;
import com.nhom16.VNTech.service.PCBuildService;
import com.nhom16.VNTech.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pc-builds")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PCBuildController {

    private final PCBuildService pcBuildService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

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

    @PostMapping("")
    public ResponseEntity<APIResponse<PCBuildResponseDto>> createPCBuild(
            HttpServletRequest request,
            @RequestBody PCBuildRequestDto requestDto) {

        Long userId = extractUserIdFromRequest(request);
        PCBuildResponseDto build = pcBuildService.createPCBuild(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(build, "Tạo cấu hình PC thành công"));
    }

    @GetMapping("")
    public ResponseEntity<APIResponse<List<PCBuildResponseDto>>> getAllPCBuilds(
            HttpServletRequest request) {

        Long userId = extractUserIdFromRequest(request);
        List<PCBuildResponseDto> builds = pcBuildService.getAllPCBuilds(userId);
        return ResponseEntity.ok(APIResponse.success(builds, "Lấy danh sách cấu hình PC thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<PCBuildResponseDto>> getPCBuildById(
            HttpServletRequest request,
            @PathVariable Long id) {

        Long userId = extractUserIdFromRequest(request);
        PCBuildResponseDto build = pcBuildService.getPCBuildById(userId, id);
        return ResponseEntity.ok(APIResponse.success(build, "Lấy chi tiết cấu hình PC thành công"));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<APIResponse<PCBuildResponseDto>> addComponentToBuild(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody PCBuildItemRequestDto requestDto) {

        Long userId = extractUserIdFromRequest(request);
        PCBuildResponseDto build = pcBuildService.addComponentToBuild(userId, id, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(build, "Đã thêm linh kiện vào cấu hình"));
    }

    @PutMapping("/{buildId}/items/{itemId}")
    public ResponseEntity<APIResponse<PCBuildResponseDto>> updateComponentInBuild(
            HttpServletRequest request,
            @PathVariable Long buildId,
            @PathVariable Long itemId,
            @RequestBody UpdatePCBuildItemRequestDto requestDto) {

        Long userId = extractUserIdFromRequest(request);
        PCBuildResponseDto build = pcBuildService.updateComponentInBuild(userId, buildId, itemId, requestDto);
        return ResponseEntity.ok(APIResponse.success(build, "Cập nhật linh kiện thành công"));
    }

    @DeleteMapping("/{buildId}/items/{itemId}")
    public ResponseEntity<APIResponse<Void>> removeComponentFromBuild(
            HttpServletRequest request,
            @PathVariable Long buildId,
            @PathVariable Long itemId) {

        Long userId = extractUserIdFromRequest(request);
        pcBuildService.removeComponentFromBuild(userId, buildId, itemId);
        return ResponseEntity.ok(APIResponse.success(null, "Đã xóa linh kiện khỏi cấu hình"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deletePCBuild(
            HttpServletRequest request,
            @PathVariable Long id) {

        Long userId = extractUserIdFromRequest(request);
        pcBuildService.deletePCBuild(userId, id);
        return ResponseEntity.ok(APIResponse.success(null, "Đã xóa cấu hình PC"));
    }

    @PostMapping("/{id}/add-to-cart")
    public ResponseEntity<APIResponse<CartResponseDto>> addBuildToCart(
            HttpServletRequest request,
            @PathVariable Long id) {

        Long userId = extractUserIdFromRequest(request);
        CartResponseDto cart = pcBuildService.addBuildToCart(userId, id);
        return ResponseEntity.ok(APIResponse.success(cart, "Đã thêm cấu hình vào giỏ hàng"));
    }
}
