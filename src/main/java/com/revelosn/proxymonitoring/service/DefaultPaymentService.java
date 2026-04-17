package com.revelosn.proxymonitoring.service;

import com.revelosn.proxymonitoring.exception.ResourceNotFoundException;
import com.revelosn.proxymonitoring.exception.SimulatedPaymentFailureException;
import com.revelosn.proxymonitoring.model.PaymentRecord;
import com.revelosn.proxymonitoring.model.PaymentStatus;
import com.revelosn.proxymonitoring.repository.OrderRepository;
import com.revelosn.proxymonitoring.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

@Service
public class DefaultPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentFailureDecider paymentFailureDecider;
    private final Clock clock;

    public DefaultPaymentService(PaymentRepository paymentRepository,
                                 OrderRepository orderRepository,
                                 PaymentFailureDecider paymentFailureDecider,
                                 Clock clock) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentFailureDecider = paymentFailureDecider;
        this.clock = clock;
    }

    @Override
    public PaymentRecord processPayment(Long orderId, BigDecimal amount, String paymentMethod) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("El método de pago es obligatorio");
        }

        orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el recurso solicitado"));

        if (paymentFailureDecider.shouldFail()) {
            throw new SimulatedPaymentFailureException(
                    "La operación de pago falló de forma intencional para la simulación");
        }

        PaymentRecord paymentRecord = new PaymentRecord(null, orderId, amount, paymentMethod.trim(),
                PaymentStatus.SUCCESS, Instant.now(clock));
        return paymentRepository.save(paymentRecord);
    }

    @Override
    public PaymentRecord getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el recurso solicitado"));
    }
}

