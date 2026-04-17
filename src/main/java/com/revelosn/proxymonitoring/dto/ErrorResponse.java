package com.revelosn.proxymonitoring.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        boolean success,
        String message,
        Instant timestamp,
        String requestId,
        String errorCode,
        Map<String, Object> details
) {

    public static ErrorResponse of(String message, String requestId, String errorCode, Map<String, Object> details) {
        return new ErrorResponse(false, message, Instant.now(), requestId, errorCode, details);
    }
}

