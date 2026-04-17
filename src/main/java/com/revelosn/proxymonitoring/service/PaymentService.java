package com.revelosn.proxymonitoring.service;

import com.revelosn.proxymonitoring.model.PaymentRecord;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentRecord processPayment(Long orderId, BigDecimal amount, String paymentMethod);

    PaymentRecord getPaymentById(Long paymentId);
}

