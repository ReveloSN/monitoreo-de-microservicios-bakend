package com.revelosn.proxymonitoring.dto;

public record SimulationServiceResult(
        String serviceId,
        int totalCalls,
        int successCalls,
        int errorCalls
) {
}

