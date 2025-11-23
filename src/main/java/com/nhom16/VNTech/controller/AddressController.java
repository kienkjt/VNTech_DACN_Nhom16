package com.nhom16.VNTech.controller;

import com.nhom16.VNTech.dto.APIResponse;
import com.nhom16.VNTech.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
@CrossOrigin(origins = "http://localhost:3000")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/provinces")
    public ResponseEntity<APIResponse<List<Map<String, Object>>>> getAllProvinces() {
        List<Map<String, Object>> provinces = addressService.getAllProvinces();
        return ResponseEntity.ok(APIResponse.success(provinces, "Lấy danh sách tỉnh/thành công"));
    }

    @GetMapping("/districts")
    public ResponseEntity<APIResponse<List<Map<String, Object>>>> getDistrictsByProvince(
            @RequestParam String provinceCode) {

        List<Map<String, Object>> districts = addressService.getDistrictsByProvince(provinceCode);
        return ResponseEntity.ok(APIResponse.success(districts, "Lấy danh sách quận/huyện theo tỉnh thành công"));
    }

    @GetMapping("/wards")
    public ResponseEntity<APIResponse<List<Map<String, Object>>>> getWardsByDistrict(
            @RequestParam String districtCode) {
        List<Map<String, Object>> wards = addressService.getWardsByDistrict(districtCode);
        return ResponseEntity.ok(APIResponse.success(wards, "Lấy danh sách phường/xã theo huyện thành công"));
    }
}
