package com.revelosn.proxymonitoring.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ServiceOperationRequest(
        @NotNull(message = "Los parámetros de la operación son obligatorios")
        Map<String, Object> params
) {
}

