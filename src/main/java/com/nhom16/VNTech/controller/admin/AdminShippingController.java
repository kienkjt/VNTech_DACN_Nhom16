package com.nhom16.VNTech.controller.admin;

import com.nhom16.VNTech.dto.shipping.ShippingDistanceDto;
import com.nhom16.VNTech.service.ShippingFeeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/shipping")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminShippingController {

    private final ShippingFeeService shippingFeeService;

    @GetMapping("/distances")
    @Operation(summary = "Lấy danh sách tất cả tỉnh", description = "Lấy danh sách tất cả các tỉnh với thông tin phí ship ")
    public ResponseEntity<List<ShippingDistanceDto>> getAllDistances() {
        List<ShippingDistanceDto> distances = shippingFeeService.getAllShippingDistances();
        return ResponseEntity.ok(distances);
    }

    @GetMapping("/distances/{province}")
    @Operation(summary = "Lấy thông tin ship theo tỉnh", description = "Lấy thông tin phí ship của một tỉnh")
    public ResponseEntity<ShippingDistanceDto> getDistanceByProvince(@PathVariable String province) {
        ShippingDistanceDto distance = shippingFeeService.getShippingDistanceByProvince(province);
        return ResponseEntity.ok(distance);
    }
}
