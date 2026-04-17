package com.revelosn.proxymonitoring.dto;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        String message,
        Instant timestamp,
        String requestId,
        T data
) {

    public static <T> ApiResponse<T> success(String message, String requestId, T data) {
        return new ApiResponse<>(true, message, Instant.now(), requestId, data);
    }
}

