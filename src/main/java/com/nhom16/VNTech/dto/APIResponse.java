package com.nhom16.VNTech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> APIResponse<T> success(T data, String message) {
        return new APIResponse<>(true, message, data);
    }

    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>(true, "Thành công", data);
    }

    public static <T> APIResponse<T> error(String message) {
        return new APIResponse<>(false, message, null);
    }

    public static <T> APIResponse<T> error(String message, T data) {
        return new APIResponse<>(false, message, data);
    }
}