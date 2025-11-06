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

        log.info("Uploading product image to folder: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "image"
                    ));

            log.info("Successfully uploaded product image: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Error uploading product image to Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to upload product image", e);
        }
    }

    @Override
    public Map uploadUserAvatar(MultipartFile file, Long userId) throws IOException {
        validateImageFile(file);

        String folderName = "avatars/" + userId;

        log.info("Uploading user avatar to folder: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "image"
                            // Không dùng transformation để tránh lỗi
                    ));

            log.info("Successfully uploaded user avatar: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Error uploading user avatar to Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to upload user avatar", e);
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

        log.info("Uploading product image with transformation to folder: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            log.info("Successfully uploaded product image with transformation: {}", uploadResult.get("public_id"));
            return uploadResult;
        } catch (IOException e) {
            log.error("Error uploading product image with transformation: {}", e.getMessage());
            throw new IOException("Failed to upload product image", e);
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
            log.info("Successfully uploaded user avatar with transformation: {}", uploadResult.get("public_id"));
            return uploadResult;
        } catch (IOException e) {
            log.error("Error uploading user avatar with transformation: {}", e.getMessage());
            throw new IOException("Failed to upload user avatar", e);
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

            log.info("Successfully uploaded product image with string transformation: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Error uploading product image with string transformation: {}", e.getMessage());
            throw new IOException("Failed to upload product image", e);
        }
    }

    public Map uploadUserAvatarWithString(MultipartFile file, Long userId) throws IOException {
        validateImageFile(file);

        String folderName = "avatars/" + userId;

        log.info("Uploading user avatar with string transformation to folder: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "image",
                            "transformation", "c_fill,w_200,h_200,g_face,q_auto,f_jpg"
                    ));

            log.info("Successfully uploaded user avatar with string transformation: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Error uploading user avatar with string transformation: {}", e.getMessage());
            throw new IOException("Failed to upload user avatar", e);
        }
    }

    // Upload file thông thường (cho các mục đích khác)
    public Map uploadFile(MultipartFile file, String folderName) throws IOException {
        validateImageFile(file);

        log.info("Uploading file to folder: {}", folderName);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderName,
                            "resource_type", "auto" // Tự động detect loại file
                    ));

            log.info("Successfully uploaded file: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to upload file", e);
        }
    }

    @Override
    public void deleteImage(String publicId) throws IOException {
        if (publicId == null || publicId.trim().isEmpty()) {
            throw new RuntimeException("Public ID cannot be null or empty");
        }

        log.info("Deleting image from Cloudinary with publicId: {}", publicId);

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            if ("ok".equals(result.get("result"))) {
                log.info("Successfully deleted image with publicId: {}", publicId);
            } else {
                log.warn("Delete operation returned: {}", result.get("result"));
                throw new IOException("Failed to delete image from Cloudinary");
            }

        } catch (IOException e) {
            log.error("Error deleting image from Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to delete image from Cloudinary", e);
        }
    }

    // Xóa nhiều images
    public void deleteImages(List<String> publicIds) throws IOException {
        if (publicIds == null || publicIds.isEmpty()) {
            return;
        }

        log.info("Deleting {} images from Cloudinary", publicIds.size());

        for (String publicId : publicIds) {
            try {
                deleteImage(publicId);
            } catch (IOException e) {
                log.error("Failed to delete image with publicId: {}, error: {}", publicId, e.getMessage());
                // Continue with other images even if one fails
            }
        }
    }

    // Lấy thông tin image từ Cloudinary
    public Map getImageInfo(String publicId) throws IOException {
        if (publicId == null || publicId.trim().isEmpty()) {
            throw new RuntimeException("Public ID cannot be null or empty");
        }

        log.info("Getting image info for publicId: {}", publicId);

        try {
            Map result = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            log.info("Successfully retrieved image info for: {}", publicId);
            return result;
        } catch (Exception e) {
            log.error("Error getting image info from Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to get image info from Cloudinary", e);
        }
    }

    // Tạo signed URL cho upload (nếu cần bảo mật)
    public Map generateUploadSignature(Map<String, Object> params) {
        try {
            // Cloudinary sẽ tự động sử dụng API secret từ config
            return cloudinary.api().createUploadPreset(params);
        } catch (Exception e) {
            log.error("Error generating upload signature: {}", e.getMessage());
            throw new RuntimeException("Failed to generate upload signature");
        }
    }

    // Validate image file
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("File must be an image. Received: " + contentType);
        }

        // Check file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size must be less than 5MB. Current size: " + file.getSize() + " bytes");
        }

        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp");

            if (!allowedExtensions.contains(extension)) {
                throw new RuntimeException("Unsupported file format: " + extension +
                        ". Supported formats: " + String.join(", ", allowedExtensions));
            }
        }

        log.debug("File validation passed: {} ({} bytes, {})",
                originalFilename, file.getSize(), contentType);
    }

    // Helper method để extract publicId từ URL
    public String extractPublicIdFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        try {
            // Cloudinary URL format: https://res.cloudinary.com/cloud_name/image/upload/v1234567/public_id.jpg
            String[] parts = url.split("/upload/");
            if (parts.length > 1) {
                String afterUpload = parts[1];
                // Remove version if present
                if (afterUpload.startsWith("v")) {
                    afterUpload = afterUpload.substring(afterUpload.indexOf('/') + 1);
                }
                // Remove file extension
                int lastDotIndex = afterUpload.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    afterUpload = afterUpload.substring(0, lastDotIndex);
                }
                return afterUpload;
            }
        } catch (Exception e) {
            log.warn("Could not extract publicId from URL: {}", url);
        }

        return null;
    }

    // Kiểm tra xem file có tồn tại trên Cloudinary không
    public boolean imageExists(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return false;
        }

        try {
            Map info = getImageInfo(publicId);
            return info != null && !info.isEmpty();
        } catch (Exception e) {
            log.debug("Image does not exist or error checking: {}", publicId);
            return false;
        }
    }
}