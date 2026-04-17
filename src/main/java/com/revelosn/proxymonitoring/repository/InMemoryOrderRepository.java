package com.revelosn.proxymonitoring.repository;

import com.revelosn.proxymonitoring.model.OrderRecord;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final ConcurrentHashMap<Long, OrderRecord> orders = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public OrderRecord save(OrderRecord order) {
        Long id = order.id() == null ? sequence.getAndIncrement() : order.id();
        sequence.updateAndGet(current -> Math.max(current, id + 1));
        OrderRecord toStore = order.id() == null ? order.withId(id) : order;
        orders.put(id, toStore);
        return toStore;
    }

    @Override
    public Optional<OrderRecord> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<OrderRecord> findAll() {
        return orders.values().stream()
                .sorted(Comparator.comparing(OrderRecord::createdAt).reversed())
                .toList();
    }
}
