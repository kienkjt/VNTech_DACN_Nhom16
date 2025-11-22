package com.nhom16.VNTech.service.Impl;

import com.nhom16.VNTech.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final RestTemplate restTemplate;

    private static final String PROVINCES_URL = "https://vn-public-apis.fpo.vn/provinces/getAll?limit=-1";

    private static final String DISTRICTS_BY_PROVINCE_URL = "https://vn-public-apis.fpo.vn/districts/getByProvince?provinceCode=%s&limit=-1";

    private static final String WARDS_BY_DISTRICT_URL = "https://vn-public-apis.fpo.vn/wards/getByDistrict?districtCode=%s&limit=-1";

    public AddressServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Map<String, Object>> getAllProvinces() {
        return fetchListFromApi(PROVINCES_URL);
    }

    @Override
    public List<Map<String, Object>> getDistrictsByProvince(String provinceCode) {
        String url = String.format(DISTRICTS_BY_PROVINCE_URL, provinceCode);
        return fetchListFromApi(url);
    }

    @Override
    public List<Map<String, Object>> getWardsByDistrict(String districtCode) {
        String url = String.format(WARDS_BY_DISTRICT_URL, districtCode);
        return fetchListFromApi(url);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchListFromApi(String url) {
        try {
            Object resp = restTemplate.getForObject(url, Object.class);
            if (resp == null) return List.of();

            if (resp instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) resp;

                Object data = map.get("data");
                if (data instanceof Map) {
                    Object innerData = ((Map<String, Object>) data).get("data");
                    if (innerData instanceof List) {
                        return (List<Map<String, Object>>) innerData;
                    }
                }

                if (data instanceof List) {
                    return (List<Map<String, Object>>) data;
                }
            }

            logger.warn("API {} trả về dữ liệu không mong đợi", url);
            return List.of();

        } catch (Exception ex) {
            logger.error("Lỗi khi gọi API {}: {}", url, ex.getMessage());
            return List.of();
        }
    }
}
