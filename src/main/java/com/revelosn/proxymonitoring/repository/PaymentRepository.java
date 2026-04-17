package com.revelosn.proxymonitoring.repository;

import com.revelosn.proxymonitoring.model.PaymentRecord;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    PaymentRecord save(PaymentRecord payment);

    Optional<PaymentRecord> findById(Long id);

    List<PaymentRecord> findAll();
}

