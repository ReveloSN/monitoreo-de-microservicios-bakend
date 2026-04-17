package com.revelosn.proxymonitoring.util;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class OperationParameterResolver {

    private OperationParameterResolver() {
    }

    public static Map<String, Object> asMap(Object... params) {
        if (params == null || params.length == 0 || params[0] == null) {
            return Map.of();
        }
        if (params[0] instanceof Map<?, ?> rawMap) {
            LinkedHashMap<String, Object> normalized = new LinkedHashMap<>();
            rawMap.forEach((key, value) -> normalized.put(String.valueOf(key), value));
            return Map.copyOf(normalized);
        }
        LinkedHashMap<String, Object> indexedParams = new LinkedHashMap<>();
        for (int index = 0; index < params.length; index++) {
            indexedParams.put("param" + index, params[index]);
        }
        return Map.copyOf(indexedParams);
    }

    public static Long requireLong(Map<String, Object> params, String... keys) {
        return convertToLong(requireValue(params, keys), keys[0]);
    }

    public static Integer requireInt(Map<String, Object> params, String... keys) {
        return convertToInt(requireValue(params, keys), keys[0]);
    }

    public static String requireString(Map<String, Object> params, String... keys) {
        Object value = requireValue(params, keys);
        String normalized = String.valueOf(value).trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("El parámetro '" + keys[0] + "' es obligatorio");
        }
        return normalized;
    }

    public static BigDecimal requireBigDecimal(Map<String, Object> params, String... keys) {
        Object value = requireValue(params, keys);
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El parámetro '" + keys[0] + "' debe ser numérico");
        }
    }

    private static Object requireValue(Map<String, Object> params, String... keys) {
        Objects.requireNonNull(params, "params");
        for (String key : keys) {
            if (params.containsKey(key) && params.get(key) != null) {
                return params.get(key);
            }
        }
        throw new IllegalArgumentException("El parámetro '" + keys[0] + "' es obligatorio");
    }

    private static Long convertToLong(Object value, String key) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El parámetro '" + key + "' debe ser numérico");
        }
    }

    private static Integer convertToInt(Object value, String key) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El parámetro '" + key + "' debe ser numérico");
        }
    }
}

