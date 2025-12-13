package com.nhom16.VNTech.service;

import com.nhom16.VNTech.dto.cart.CartResponseDto;
import com.nhom16.VNTech.dto.pcbuild.*;

import java.util.List;

public interface PCBuildService {
    PCBuildResponseDto createPCBuild(Long userId, PCBuildRequestDto request);

    PCBuildResponseDto getPCBuildById(Long userId, Long buildId);

    List<PCBuildResponseDto> getAllPCBuilds(Long userId);

    PCBuildResponseDto addComponentToBuild(Long userId, Long buildId, PCBuildItemRequestDto request);

    PCBuildResponseDto updateComponentInBuild(Long userId, Long buildId, Long itemId, UpdatePCBuildItemRequestDto request);

    void removeComponentFromBuild(Long userId, Long buildId, Long itemId);

    void deletePCBuild(Long userId, Long buildId);

    CartResponseDto addBuildToCart(Long userId, Long buildId);
}
