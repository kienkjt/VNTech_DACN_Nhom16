package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.dto.cart.AddToCartRequestDto;
import com.nhom16.VNTech.dto.cart.CartResponseDto;
import com.nhom16.VNTech.dto.pcbuild.*;
import com.nhom16.VNTech.dto.product.ProductDto;
import com.nhom16.VNTech.entity.*;
import com.nhom16.VNTech.repository.*;
import com.nhom16.VNTech.service.CartService;
import com.nhom16.VNTech.service.PCBuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PCBuildServiceImpl implements PCBuildService {

    private final PCBuildRepository pcBuildRepository;
    private final PCBuildItemRepository pcBuildItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    // Danh sách các loại linh kiện
    private static final List<String> ALLOWED_COMPONENT_TYPES = Arrays.asList(
            "CPU", "GPU", "RAM", "SSD", "HDD", "Mainboard", "PSU", "Case",
            "Tản nhiệt", "Màn hình", "Bàn phím", "Chuột", "Tai nghe",
            "Webcam", "Ổ đĩa quang", "UPS", "Card mạng");

    @Override
    public PCBuildResponseDto createPCBuild(Long userId, PCBuildRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (request.getBuildName() == null || request.getBuildName().trim().isEmpty()) {
            throw new RuntimeException("Tên cấu hình không được để trống");
        }

        PCBuild pcBuild = new PCBuild();
        pcBuild.setBuildName(request.getBuildName());
        pcBuild.setUser(user);
        pcBuild.setTotalCost(0L);

        PCBuild savedBuild = pcBuildRepository.save(pcBuild);
        return mapToResponseDto(savedBuild);
    }

    @Override
    @Transactional(readOnly = true)
    public PCBuildResponseDto getPCBuildById(Long userId, Long buildId) {
        PCBuild pcBuild = pcBuildRepository.findByUserIdAndId(userId, buildId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình PC hoặc không có quyền truy cập"));

        return mapToResponseDto(pcBuild);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PCBuildResponseDto> getAllPCBuilds(Long userId) {
        List<PCBuild> builds = pcBuildRepository.findByUserId(userId);
        return builds.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PCBuildResponseDto addComponentToBuild(Long userId, Long buildId, PCBuildItemRequestDto request) {
        if (!ALLOWED_COMPONENT_TYPES.contains(request.getComponentType())) {
            throw new RuntimeException("Loại linh kiện không hợp lệ: " + request.getComponentType());
        }

        PCBuild pcBuild = pcBuildRepository.findByUserIdAndId(userId, buildId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình PC hoặc không có quyền truy cập"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException(
                    String.format("Số lượng không đủ trong kho. Chỉ còn %d sản phẩm", product.getStock()));
        }

        // Kiểm tra xem đã có linh kiện cùng loại chưa
        pcBuildItemRepository.findByPcBuildIdAndComponentType(buildId, request.getComponentType())
                .ifPresent(existingItem -> {
                    // Xóa linh kiện cũ nếu đã tồn tại
                    pcBuildItemRepository.delete(existingItem);
                });

        // Tạo item mới
        PCBuildItem item = new PCBuildItem();
        item.setPcBuild(pcBuild);
        item.setProduct(product);
        item.setComponentType(request.getComponentType());
        item.setQuantity(request.getQuantity());
        item.setPrice(product.getSalePrice());

        pcBuildItemRepository.save(item);

        // Cập nhật tổng chi phí
        updateTotalCost(pcBuild);

        return mapToResponseDto(pcBuild);
    }

    @Override
    public PCBuildResponseDto updateComponentInBuild(Long userId, Long buildId, Long itemId,
            UpdatePCBuildItemRequestDto request) {
        PCBuild pcBuild = pcBuildRepository.findByUserIdAndId(userId, buildId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình PC hoặc không có quyền truy cập"));

        PCBuildItem item = pcBuildItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy linh kiện trong cấu hình"));

        if (!item.getPcBuild().getId().equals(buildId)) {
            throw new RuntimeException("Linh kiện không thuộc cấu hình này");
        }

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        if (request.getQuantity() > item.getProduct().getStock()) {
            throw new RuntimeException(
                    String.format("Số lượng không đủ trong kho. Chỉ còn %d sản phẩm",
                            item.getProduct().getStock()));
        }

        item.setQuantity(request.getQuantity());
        pcBuildItemRepository.save(item);

        // Cập nhật tổng chi phí
        updateTotalCost(pcBuild);

        return mapToResponseDto(pcBuild);
    }

    @Override
    public void removeComponentFromBuild(Long userId, Long buildId, Long itemId) {
        PCBuild pcBuild = pcBuildRepository.findByUserIdAndId(userId, buildId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình PC hoặc không có quyền truy cập"));

        PCBuildItem item = pcBuildItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy linh kiện trong cấu hình"));

        if (!item.getPcBuild().getId().equals(buildId)) {
            throw new RuntimeException("Linh kiện không thuộc cấu hình này");
        }

        pcBuildItemRepository.delete(item);

        // Cập nhật tổng chi phí
        updateTotalCost(pcBuild);
    }

    @Override
    public void deletePCBuild(Long userId, Long buildId) {
        PCBuild pcBuild = pcBuildRepository.findByUserIdAndId(userId, buildId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình PC hoặc không có quyền truy cập"));

        pcBuildRepository.delete(pcBuild);
    }

    @Override
    public CartResponseDto addBuildToCart(Long userId, Long buildId) {
        PCBuild pcBuild = pcBuildRepository.findByUserIdAndId(userId, buildId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cấu hình PC hoặc không có quyền truy cập"));

        if (pcBuild.getPcBuildItems().isEmpty()) {
            throw new RuntimeException("Cấu hình PC chưa có linh kiện nào");
        }

        // Thêm từng linh kiện vào giỏ hàng
        for (PCBuildItem item : pcBuild.getPcBuildItems()) {
            AddToCartRequestDto cartRequest = new AddToCartRequestDto();
            cartRequest.setProductId(item.getProduct().getId());
            cartRequest.setQuantity(item.getQuantity());

            cartService.addToCart(userId, cartRequest);
        }

        return cartService.getCartByUserId(userId);
    }

    // Helper methods
    private void updateTotalCost(PCBuild pcBuild) {
        Long totalCost = pcBuild.getPcBuildItems().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();

        pcBuild.setTotalCost(totalCost);
        pcBuildRepository.save(pcBuild);
    }

    private PCBuildResponseDto mapToResponseDto(PCBuild pcBuild) {
        PCBuildResponseDto dto = new PCBuildResponseDto();
        dto.setId(pcBuild.getId());
        dto.setBuildName(pcBuild.getBuildName());
        dto.setTotalCost(pcBuild.getTotalCost());
        dto.setCreatedAt(pcBuild.getCreatedAt());
        dto.setUpdatedAt(pcBuild.getUpdatedAt());

        List<PCBuildItemResponseDto> items = pcBuild.getPcBuildItems().stream()
                .map(this::mapToItemResponseDto)
                .collect(Collectors.toList());
        dto.setItems(items);

        return dto;
    }

    private PCBuildItemResponseDto mapToItemResponseDto(PCBuildItem item) {
        PCBuildItemResponseDto dto = new PCBuildItemResponseDto();
        dto.setId(item.getId());
        dto.setComponentType(item.getComponentType());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());

        // Map product
        Product product = item.getProduct();
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setProductName(product.getProductName());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setBrand(product.getBrand());
        productDto.setModel(product.getModel());
        productDto.setStock(product.getStock());

        // Get main image
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            productDto.setMainImage(product.getImages().get(0).getImageUrl());
        }

        dto.setProduct(productDto);
        return dto;
    }
}
