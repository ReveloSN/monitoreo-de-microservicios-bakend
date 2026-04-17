package com.revelosn.proxymonitoring.repository;

import com.revelosn.proxymonitoring.model.OrderRecord;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    OrderRecord save(OrderRecord order);

    Optional<OrderRecord> findById(Long id);

    List<OrderRecord> findAll();
}

