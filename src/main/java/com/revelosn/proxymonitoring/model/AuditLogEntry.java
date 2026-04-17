package com.revelosn.proxymonitoring.model;

import java.time.Instant;
import java.util.Map;

public record AuditLogEntry(
        String serviceId,
        String operation,
        long durationMs,
        AuditStatus status,
        String requestId,
        Instant timestamp,
        Map<String, Object> inputParams,
        Object response,
        String errorMessage,
        String stackTrace,
        String httpPath
) {
}

