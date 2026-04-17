package com.revelosn.proxymonitoring.dto;

import java.util.List;

public record MetricsSummaryResponse(
        long totalCalls,
        double errorRate,
        double averageResponseTime,
        List<ServiceMetricsResponse> services
) {
}

