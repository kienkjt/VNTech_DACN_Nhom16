package com.nhom16.VNTech.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map uploadFile(MultipartFile file, String folderName) throws IOException;
    Map uploadVideo(MultipartFile file, String folderName) throws IOException;
}