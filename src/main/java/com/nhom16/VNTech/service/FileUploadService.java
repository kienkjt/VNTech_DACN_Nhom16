// FileUploadService.java (Thêm methods đơn giản)
package com.nhom16.VNTech.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

public interface FileUploadService {
    Map uploadProductImage(MultipartFile file, Long productId) throws IOException;
    Map uploadUserAvatar(MultipartFile file, Long userId) throws IOException;
    Map uploadProductImageSimple(MultipartFile file, Long productId) throws IOException;
    Map uploadUserAvatarSimple(MultipartFile file, Long userId) throws IOException;

    void deleteImage(String publicId) throws IOException;
}