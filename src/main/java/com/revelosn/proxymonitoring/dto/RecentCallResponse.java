package com.revelosn.proxymonitoring.dto;

import java.time.Instant;

public record RecentCallResponse(
        String requestId,
        Instant timestamp,
        long durationMs,
        String status,
        String operation
) {
}

