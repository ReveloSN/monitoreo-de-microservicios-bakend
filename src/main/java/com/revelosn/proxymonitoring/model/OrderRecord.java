package com.revelosn.proxymonitoring.model;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderRecord(
        Long id,
        String customerName,
        Long itemId,
        int quantity,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt
) {

    public OrderRecord withId(Long newId) {
        return new OrderRecord(newId, customerName, itemId, quantity, totalAmount, status, createdAt);
    }
}

