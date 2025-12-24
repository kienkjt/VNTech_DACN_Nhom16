package com.nhom16.VNTech.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhom16.VNTech.entity.*;
import com.nhom16.VNTech.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiDataService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Lấy thông tin sản phẩm cho khách hàng
     */
    public Map<String, Object> getProductContext(Long userId) {
        Map<String, Object> context = new LinkedHashMap<>();

        // Top 10 sản phẩm mới nhất
        Pageable topProductsPage = PageRequest.of(0, 10);
        List<Product> topProducts = productRepository.findByOrderByCreatedAtDesc(topProductsPage).getContent();

        context.put("topProducts", topProducts.stream()
                .map(p -> Map.of(
                        "id", p.getId(),
                        "name", p.getProductName(),
                        "price", p.getSalePrice(),
                        "originalPrice", p.getOriginalPrice(),
                        "category", p.getCategory() != null ? p.getCategory().getName() : "N/A",
                        "brand", p.getBrand() != null ? p.getBrand() : "N/A",
                        "inStock", p.getStock() > 0,
                        "stock", p.getStock()
                ))
                .collect(Collectors.toList())
        );

        // Danh mục sản phẩm
        List<Category> categories = categoryRepository.findAll();
        context.put("categories", categories.stream()
                .map(c -> Map.of("id", c.getId(), "name", c.getName()))
                .collect(Collectors.toList())
        );

        // Sản phẩm còn hàng
        Pageable inStockPage = PageRequest.of(0, 5);
        List<Product> inStockProducts = productRepository.findByStockGreaterThan(0, inStockPage).getContent();
        context.put("inStockCount", inStockProducts.size());

        return context;
    }

    /**
     * Tìm sản phẩm theo từ khóa
     */
    public Map<String, Object> searchProducts(String keyword, Long userId) {
        Map<String, Object> context = new LinkedHashMap<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            context.put("hasResults", false);
            return context;
        }

        Pageable pageable = PageRequest.of(0, 5);
        List<Product> products = productRepository
                .findByProductNameContainingIgnoreCase(keyword.trim(), pageable)
                .getContent();

        context.put("hasResults", !products.isEmpty());
        context.put("keyword", keyword);
        context.put("resultCount", products.size());
        context.put("products", products.stream()
                .map(p -> Map.of(
                        "id", p.getId(),
                        "name", p.getProductName(),
                        "price", p.getSalePrice(),
                        "brand", p.getBrand() != null ? p.getBrand() : "N/A",
                        "inStock", p.getStock() > 0
                ))
                .collect(Collectors.toList())
        );

        return context;
    }

    /**
     * Tìm sản phẩm theo khoảng giá
     */
    public Map<String, Object> getProductsByPriceRange(Long minPrice, Long maxPrice) {
        Map<String, Object> context = new LinkedHashMap<>();

        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = productRepository
                .findByPriceRange(minPrice, maxPrice, pageable)
                .getContent();

        context.put("hasResults", !products.isEmpty());
        context.put("priceRange", Map.of("min", minPrice, "max", maxPrice));
        context.put("resultCount", products.size());
        context.put("products", products.stream()
                .map(p -> Map.of(
                        "id", p.getId(),
                        "name", p.getProductName(),
                        "price", p.getSalePrice(),
                        "category", p.getCategory() != null ? p.getCategory().getName() : "N/A",
                        "brand", p.getBrand() != null ? p.getBrand() : "N/A"
                ))
                .collect(Collectors.toList())
        );

        return context;
    }

    /**
     * Lấy lịch sử đơn hàng của khách hàng
     */
    public Map<String, Object> getOrderContext(Long userId) {
        Map<String, Object> context = new LinkedHashMap<>();

        if (userId == null) {
            context.put("authenticated", false);
            context.put("hasOrders", false);
            return context;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            context.put("hasOrders", false);
            return context;
        }

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        context.put("hasOrders", !orders.isEmpty());
        context.put("totalOrders", orders.size());

        if (!orders.isEmpty()) {
            // Đơn hàng gần nhất
            Order latestOrder = orders.get(0);
            context.put("latestOrder", Map.of(
                    "id", latestOrder.getId(),
                    "status", latestOrder.getStatus(),
                    "total", latestOrder.getTotalPrice(),
                    "date", latestOrder.getCreatedAt().toString(),
                    "itemCount", latestOrder.getOrderItems().size()
            ));

            // Sản phẩm đã mua (5 sản phẩm gần nhất)
            Set<String> purchasedProducts = orders.stream()
                    .limit(3) // Chỉ lấy 3 đơn gần nhất
                    .flatMap(o -> o.getOrderItems().stream())
                    .map(oi -> oi.getProducts().getProductName())
                    .limit(5)
                    .collect(Collectors.toSet());
            context.put("recentPurchases", purchasedProducts);
        }

        return context;
    }

    /**
     * Lấy thông tin cá nhân khách hàng
     */
    public Map<String, Object> getUserContext(Long userId) {
        Map<String, Object> context = new LinkedHashMap<>();

        if (userId == null) {
            context.put("authenticated", false);
            return context;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            context.put("authenticated", false);
            return context;
        }

        User user = userOpt.get();
        context.put("authenticated", true);
        context.put("userName", user.getFullName());
        context.put("email", user.getEmail());
        context.put("memberSince", user.getCreatedAt() != null ? user.getCreatedAt().toString() : "N/A");

        return context;
    }

    /**
     * Lấy sản phẩm theo thương hiệu
     */
    public Map<String, Object> getProductsByBrand(String brand) {
        Map<String, Object> context = new LinkedHashMap<>();

        if (brand == null || brand.trim().isEmpty()) {
            context.put("hasResults", false);
            return context;
        }

        Pageable pageable = PageRequest.of(0, 5);
        List<Product> products = productRepository
                .findByBrandContainingIgnoreCase(brand.trim(), pageable)
                .getContent();

        context.put("hasResults", !products.isEmpty());
        context.put("brand", brand);
        context.put("products", products.stream()
                .map(p -> Map.of(
                        "id", p.getId(),
                        "name", p.getProductName(),
                        "price", p.getSalePrice(),
                        "inStock", p.getStock() > 0
                ))
                .collect(Collectors.toList())
        );

        return context;
    }

    /**
     * Lấy sản phẩm theo category
     */
    public Map<String, Object> getProductsByCategory(Long categoryId) {
        Map<String, Object> context = new LinkedHashMap<>();

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            context.put("hasResults", false);
            return context;
        }

        Category category = categoryOpt.get();
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = productRepository
                .findByCategoryId(categoryId, pageable)
                .getContent();

        context.put("hasResults", !products.isEmpty());
        context.put("category", category.getName());
        context.put("productCount", products.size());
        context.put("products", products.stream()
                .map(p -> Map.of(
                        "id", p.getId(),
                        "name", p.getProductName(),
                        "price", p.getSalePrice(),
                        "brand", p.getBrand() != null ? p.getBrand() : "N/A",
                        "inStock", p.getStock() > 0
                ))
                .collect(Collectors.toList())
        );

        return context;
    }

    /**
     * Tổng hợp context đầy đủ cho AI
     */
    public Map<String, Object> getCustomerFullContext(Long userId) {
        Map<String, Object> fullContext = new LinkedHashMap<>();

        fullContext.put("user", getUserContext(userId));
        fullContext.put("orders", getOrderContext(userId));
        fullContext.put("products", getProductContext(userId));
        fullContext.put("system", Map.of(
                "storeName", "VNTech",
                "supportEmail", "support@vntech.com",
                "supportHotline", "1900-xxxx",
                "workingHours", "8:00 - 22:00 (Hàng ngày)",
                "returnPolicy", "Đổi trả miễn phí trong 30 ngày",
                "shippingInfo", "Miễn phí ship cho đơn hàng từ 500.000đ",
                "paymentMethods", "COD, Chuyển khoản, Visa/Mastercard, MoMo, ZaloPay"
        ));

        return fullContext;
    }

    /**
     * Context với search/filter
     */
    public Map<String, Object> getCustomerContextWithSearch(Long userId, String searchQuery, Long categoryId, String brand, Long minPrice, Long maxPrice) {
        Map<String, Object> fullContext = getCustomerFullContext(userId);

        // Thêm kết quả search nếu có
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            fullContext.put("searchResults", searchProducts(searchQuery, userId));
        }

        if (categoryId != null) {
            fullContext.put("categoryProducts", getProductsByCategory(categoryId));
        }

        if (brand != null && !brand.trim().isEmpty()) {
            fullContext.put("brandProducts", getProductsByBrand(brand));
        }

        if (minPrice != null && maxPrice != null) {
            fullContext.put("priceRangeProducts", getProductsByPriceRange(minPrice, maxPrice));
        }

        return fullContext;
    }

    /**
     * Convert sang JSON cho AI
     */
    public String getCustomerContextJson(Long userId) {
        try {
            Map<String, Object> context = getCustomerFullContext(userId);
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(context);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * Context JSON với search
     */
    public String getCustomerContextJsonWithSearch(Long userId, String searchQuery, Long categoryId, String brand, Long minPrice, Long maxPrice) {
        try {
            Map<String, Object> context = getCustomerContextWithSearch(userId, searchQuery, categoryId, brand, minPrice, maxPrice);
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(context);
        } catch (Exception e) {
            return "{}";
        }
    }
}