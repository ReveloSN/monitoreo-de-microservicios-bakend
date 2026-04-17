package com.revelosn.proxymonitoring.service;

import com.revelosn.proxymonitoring.exception.ResourceNotFoundException;
import com.revelosn.proxymonitoring.exception.SimulatedPaymentFailureException;
import com.revelosn.proxymonitoring.model.OrderRecord;
import com.revelosn.proxymonitoring.model.OrderStatus;
import com.revelosn.proxymonitoring.model.PaymentRecord;
import com.revelosn.proxymonitoring.model.PaymentStatus;
import com.revelosn.proxymonitoring.repository.InMemoryOrderRepository;
import com.revelosn.proxymonitoring.repository.InMemoryPaymentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentServiceTest {

    @Test
    void processPaymentShouldFailWhenFailureDeciderTriggers() {
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepository = new InMemoryPaymentRepository();
        Clock clock = Clock.fixed(Instant.parse("2026-04-17T19:00:00Z"), ZoneOffset.UTC);

        orderRepository.save(new OrderRecord(1L, "Ana", 1L, 2, BigDecimal.valueOf(80), OrderStatus.CREATED,
                Instant.parse("2026-04-17T18:00:00Z")));

        PaymentService service = new DefaultPaymentService(paymentRepository, orderRepository, () -> true, clock);

        assertThrows(SimulatedPaymentFailureException.class,
                () -> service.processPayment(1L, BigDecimal.valueOf(80), "CARD"));
        assertEquals(List.of(), paymentRepository.findAll());
    }

    @Test
    void processPaymentShouldPersistRecordWhenFailureDeciderAllowsExecution() {
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryPaymentRepository paymentRepository = new InMemoryPaymentRepository();
        Clock clock = Clock.fixed(Instant.parse("2026-04-17T19:00:00Z"), ZoneOffset.UTC);

        orderRepository.save(new OrderRecord(1L, "Ana", 1L, 2, BigDecimal.valueOf(80), OrderStatus.CREATED,
                Instant.parse("2026-04-17T18:00:00Z")));

        PaymentService service = new DefaultPaymentService(paymentRepository, orderRepository, () -> false, clock);

        PaymentRecord record = service.processPayment(1L, BigDecimal.valueOf(80), "CARD");

        assertEquals(PaymentStatus.SUCCESS, record.status());
        assertEquals("CARD", record.paymentMethod());
        assertEquals(1L, record.orderId());
        assertEquals(1, paymentRepository.findAll().size());
    }

    @Test
    void getPaymentByIdShouldThrowWhenRecordDoesNotExist() {
        PaymentService service = new DefaultPaymentService(new InMemoryPaymentRepository(),
                new InMemoryOrderRepository(), () -> false,
                Clock.fixed(Instant.parse("2026-04-17T19:00:00Z"), ZoneOffset.UTC));

        assertThrows(ResourceNotFoundException.class, () -> service.getPaymentById(999L));
    }
}

