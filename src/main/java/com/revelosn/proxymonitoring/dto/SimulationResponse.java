package com.revelosn.proxymonitoring.dto;

import java.util.List;

public record SimulationResponse(
        int totalCalls,
        int successfulCalls,
        int failedCalls,
        long durationMs,
        List<SimulationServiceResult> services
) {
}

