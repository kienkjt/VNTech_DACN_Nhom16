package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.dto.shipping.ShippingDistanceDto;
import com.nhom16.VNTech.dto.shipping.ShippingFeeRequestDto;
import com.nhom16.VNTech.dto.shipping.ShippingFeeResponseDto;
import com.nhom16.VNTech.service.ShippingFeeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
@RequiredArgsConstructor
public class ShippingFeeController {

    private final ShippingFeeService shippingFeeService;

    @PostMapping("/calculate")
    @Operation(summary = "Tính phí ship", description = "Tính phí vận chuyển dựa trên tỉnh và giá trị đơn hàng")
    public ResponseEntity<APIResponse<ShippingFeeResponseDto>> calculateShippingFee(
            @RequestBody ShippingFeeRequestDto request) {

        ShippingFeeResponseDto response = shippingFeeService.calculateShippingFee(
                request.getProvince(),
                request.getOrderValue());

        return ResponseEntity.ok(
                APIResponse.success(response, "Tính phí vận chuyển thành công")
        );
    }

    @GetMapping("/provinces")
    @Operation(summary = "Lấy danh sách tỉnh", description = "Lấy danh sách tất cả các tỉnh với thông tin phí ship")
    public ResponseEntity<APIResponse<List<ShippingDistanceDto>>> getAllProvinces() {

        List<ShippingDistanceDto> provinces = shippingFeeService.getAllShippingDistances();

        return ResponseEntity.ok(
                APIResponse.success(provinces, "Lấy danh sách tỉnh thành công")
        );
    }

    @GetMapping("/provinces/{province}")
    @Operation(summary = "Lấy thông tin ship theo tỉnh", description = "Lấy thông tin phí ship của một tỉnh cụ thể")
    public ResponseEntity<APIResponse<ShippingDistanceDto>> getProvinceInfo(
            @PathVariable String province) {

        ShippingDistanceDto info = shippingFeeService.getShippingDistanceByProvince(province);

        return ResponseEntity.ok(
                APIResponse.success(info, "Lấy thông tin phí ship theo tỉnh thành công")
        );
    }
}
