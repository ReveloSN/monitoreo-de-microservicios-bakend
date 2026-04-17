package com.revelosn.proxymonitoring.dto;

import java.util.List;

public record ServiceMetricsResponse(
        String serviceId,
        long totalCalls,
        double successRate,
        double errorRate,
        double averageResponseTime,
        boolean hasIssues,
        List<RecentCallResponse> recentCalls
) {
}

