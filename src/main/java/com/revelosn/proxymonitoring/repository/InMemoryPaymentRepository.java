package com.revelosn.proxymonitoring.repository;

import com.revelosn.proxymonitoring.model.PaymentRecord;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private final ConcurrentHashMap<Long, PaymentRecord> payments = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public PaymentRecord save(PaymentRecord payment) {
        Long id = payment.id() == null ? sequence.getAndIncrement() : payment.id();
        sequence.updateAndGet(current -> Math.max(current, id + 1));
        PaymentRecord toStore = payment.id() == null ? payment.withId(id) : payment;
        payments.put(id, toStore);
        return toStore;
    }

    @Override
    public Optional<PaymentRecord> findById(Long id) {
        return Optional.ofNullable(payments.get(id));
    }

    @Override
    public List<PaymentRecord> findAll() {
        return payments.values().stream()
                .sorted(Comparator.comparing(PaymentRecord::processedAt).reversed())
                .toList();
    }
}

