package com.nhom16.VNTech.service;

import java.util.List;
import java.util.Map;

public interface AddressService {
    List<Map<String, Object>> getAllProvinces();
    List<Map<String, Object>> getDistrictsByProvince(String provinceCode);
    List<Map<String, Object>> getWardsByDistrict(String districtCode);
}

