package com.revelosn.proxymonitoring.model;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentRecord(
        Long id,
        Long orderId,
        BigDecimal amount,
        String paymentMethod,
        PaymentStatus status,
        Instant processedAt
) {

    public PaymentRecord withId(Long newId) {
        return new PaymentRecord(newId, orderId, amount, paymentMethod, status, processedAt);
    }
}
