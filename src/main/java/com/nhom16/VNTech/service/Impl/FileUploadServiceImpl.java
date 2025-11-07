package com.nhom16.VNTech.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nhom16.VNTech.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final Cloudinary cloudinary;

    @Override
    public Map uploadProductImage(MultipartFile file, Long productId) throws IOException {
        validateImageFile(file);

        String folderName = "products/" + productId;

        log.info("Tải hình ảnh lên thư mục: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "image"
                    ));

            log.info("Tải hình ảnh sản phẩm thành công: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Lỗi khi tải hình ảnh sản phẩm lên Cloudinary: {}", e.getMessage());
            throw new IOException("Tải hình ảnh sản phẩm thất bại", e);
        }
    }

    @Override
    public Map uploadUserAvatar(MultipartFile file, Long userId) throws IOException {
        validateImageFile(file);

        String folderName = "avatars/" + userId;

        log.info("Tải avatar người dùng lên thư mục: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "image"
                            // Không dùng transformation để tránh lỗi
                    ));

            log.info("Tải avatar người dùng thành công: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Lỗi khi tải avatar người dùng lên Cloudinary: {}", e.getMessage());
            throw new IOException("Tải avatar người dùng thất bại", e);
        }
    }

    // Phiên bản với transformation sử dụng Map (an toàn hơn)
    @Override
    public Map uploadProductImageSimple(MultipartFile file, Long productId) throws IOException {
        validateImageFile(file);

        String folderName = "products/" + productId;

        Map<String, Object> uploadOptions = new HashMap<>();
        uploadOptions.put("folder", folderName);
        uploadOptions.put("resource_type", "image");

        // Thêm transformation đơn giản sử dụng Map
        uploadOptions.put("transformation", Arrays.asList(
                Map.of("width", 800, "height", 800, "crop", "limit"),
                Map.of("quality", "auto"),
                Map.of("format", "jpg")
        ));

        log.info("Tải hình ảnh sản phẩm với transformation lên thư mục: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            log.info("Tải hình ảnh sản phẩm với transformation thành công: {}", uploadResult.get("public_id"));
            return uploadResult;
        } catch (IOException e) {
            log.error("Lỗi khi tải hình ảnh sản phẩm với transformation: {}", e.getMessage());
            throw new IOException("Tải hình ảnh sản phẩm thất bại", e);
        }
    }

    @Override
    public Map uploadUserAvatarSimple(MultipartFile file, Long userId) throws IOException {
        validateImageFile(file);

        String folderName = "avatars/" + userId;

        Map<String, Object> uploadOptions = new HashMap<>();
        uploadOptions.put("folder", folderName);
        uploadOptions.put("resource_type", "image");

        // Transformation đơn giản cho avatar
        uploadOptions.put("transformation", Arrays.asList(
                Map.of("width", 200, "height", 200, "crop", "fill"),
                Map.of("gravity", "face"),
                Map.of("quality", "auto"),
                Map.of("format", "jpg")
        ));

        log.info("Uploading user avatar with transformation to folder: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            log.info("Tải avatar người dùng với transformation thành công: {}", uploadResult.get("public_id"));
            return uploadResult;
        } catch (IOException e) {
            log.error("Lỗi khi tải avatar người dùng với transformation: {}", e.getMessage());
            throw new IOException("Tải avatar người dùng thất bại", e);
        }
    }

    public Map uploadProductImageWithString(MultipartFile file, Long productId) throws IOException {
        validateImageFile(file);

        String folderName = "products/" + productId;

        log.info("Uploading product image with string transformation to folder: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "image",
                            "transformation", "c_limit,w_800,h_800,q_auto,f_jpg"
                    ));

            log.info("Tải hình ảnh sản phẩm với transformation thành công: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Lỗi khi tải hình ảnh sản phẩm với transformation: {}", e.getMessage());
            throw new IOException("Tải hình ảnh sản phẩm thất bại", e);
        }
    }

    public Map uploadUserAvatarWithString(MultipartFile file, Long userId) throws IOException {
        validateImageFile(file);

        String folderName = "avatars/" + userId;

        log.info("Đang tải lên ảnh đại diện người dùng (có transformation) vào thư mục: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "image",
                            "transformation", "c_fill,w_200,h_200,g_face,q_auto,f_jpg"
                    ));

            log.info("Tải lên ảnh đại diện người dùng thành công với public_id: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Lỗi khi tải lên ảnh đại diện người dùng: {}", e.getMessage());
            throw new IOException("Không thể tải lên ảnh đại diện người dùng", e);
        }
    }

    // Upload file thông thường (cho các mục đích khác)
    public Map uploadFile(MultipartFile file, String folderName) throws IOException {
        validateImageFile(file);

        log.info("Đang tải file lên thư mục: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "auto" // Tự động nhận dạng loại file
                    ));

            log.info("Tải file thành công với public_id: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Lỗi khi tải file lên Cloudinary: {}", e.getMessage());
            throw new IOException("Không thể tải file lên Cloudinary", e);
        }
    }

    @Override
    public void deleteImage(String publicId) throws IOException {
        if (publicId == null || publicId.trim().isEmpty()) {
            throw new RuntimeException("Public ID không được để trống");
        }

        log.info("Đang xóa ảnh trên Cloudinary với publicId: {}", publicId);

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            if ("ok".equals(result.get("result"))) {
                log.info("Xóa ảnh thành công với publicId: {}", publicId);
            } else {
                log.warn("Kết quả xóa ảnh trả về: {}", result.get("result"));
                throw new IOException("Không thể xóa ảnh khỏi Cloudinary");
            }

        } catch (IOException e) {
            log.error("Lỗi khi xóa ảnh trên Cloudinary: {}", e.getMessage());
            throw new IOException("Không thể xóa ảnh khỏi Cloudinary", e);
        }
    }

    // Xóa nhiều ảnh
    public void deleteImages(List<String> publicIds) throws IOException {
        if (publicIds == null || publicIds.isEmpty()) {
            return;
        }

        log.info("Đang xóa {} ảnh trên Cloudinary", publicIds.size());

        for (String publicId : publicIds) {
            try {
                deleteImage(publicId);
            } catch (IOException e) {
                log.error("Không thể xóa ảnh với publicId: {}, lỗi: {}", publicId, e.getMessage());
                // Tiếp tục với các ảnh khác dù có lỗi
            }
        }
    }

    // Lấy thông tin ảnh từ Cloudinary
    public Map getImageInfo(String publicId) throws IOException {
        if (publicId == null || publicId.trim().isEmpty()) {
            throw new RuntimeException("Public ID không được để trống");
        }

        log.info("Đang lấy thông tin ảnh với publicId: {}", publicId);

        try {
            Map result = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            log.info("Lấy thông tin ảnh thành công cho publicId: {}", publicId);
            return result;
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin ảnh từ Cloudinary: {}", e.getMessage());
            throw new IOException("Không thể lấy thông tin ảnh từ Cloudinary", e);
        }
    }

    // Tạo upload signature (nếu cần bảo mật)
    public Map generateUploadSignature(Map<String, Object> params) {
        try {
            // Cloudinary sẽ tự động sử dụng API secret từ cấu hình
            return cloudinary.api().createUploadPreset(params);
        } catch (Exception e) {
            log.error("Lỗi khi tạo upload signature: {}", e.getMessage());
            throw new RuntimeException("Không thể tạo upload signature");
        }
    }

    // Validate file ảnh
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File bị trống");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("File phải là ảnh. Nhận được: " + contentType);
        }

        // Giới hạn dung lượng file (tối đa 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Dung lượng file phải nhỏ hơn 5MB. Kích thước hiện tại: " + file.getSize() + " bytes");
        }

        // Kiểm tra phần mở rộng
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp");

            if (!allowedExtensions.contains(extension)) {
                throw new RuntimeException("Định dạng file không được hỗ trợ: " + extension +
                        ". Các định dạng được hỗ trợ: " + String.join(", ", allowedExtensions));
            }
        }

        log.debug("Xác thực file thành công: {} ({} bytes, {})",
                originalFilename, file.getSize(), contentType);
    }

    // Helper method để trích xuất publicId từ URL
    public String extractPublicIdFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        try {
            // URL Cloudinary có dạng: https://res.cloudinary.com/cloud_name/image/upload/v1234567/public_id.jpg
            String[] parts = url.split("/upload/");
            if (parts.length > 1) {
                String afterUpload = parts[1];
                // Bỏ phần version nếu có
                if (afterUpload.startsWith("v")) {
                    afterUpload = afterUpload.substring(afterUpload.indexOf('/') + 1);
                }
                // Bỏ phần đuôi file
                int lastDotIndex = afterUpload.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    afterUpload = afterUpload.substring(0, lastDotIndex);
                }
                return afterUpload;
            }
        } catch (Exception e) {
            log.warn("Không thể trích xuất publicId từ URL: {}", url);
        }

        return null;
    }

    // Kiểm tra xem ảnh có tồn tại trên Cloudinary không
    public boolean imageExists(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return false;
        }

        try {
            Map info = getImageInfo(publicId);
            return info != null && !info.isEmpty();
        } catch (Exception e) {
            log.debug("Ảnh không tồn tại hoặc lỗi khi kiểm tra: {}", publicId);
            return false;
        }
    }
}