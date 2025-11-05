// FileUploadResponseDTO.java
package com.nhom16.VNTech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDto {
    private String publicId;
    private String url;
    private String format;
    private Long bytes;
    private Integer width;
    private Integer height;
    private String resourceType;
}