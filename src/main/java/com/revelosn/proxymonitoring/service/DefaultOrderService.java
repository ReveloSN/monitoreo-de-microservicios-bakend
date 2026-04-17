package com.revelosn.proxymonitoring.service;

import com.revelosn.proxymonitoring.exception.ResourceNotFoundException;
import com.revelosn.proxymonitoring.model.InventoryItem;
import com.revelosn.proxymonitoring.model.OrderRecord;
import com.revelosn.proxymonitoring.model.OrderStatus;
import com.revelosn.proxymonitoring.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final Clock clock;

    public DefaultOrderService(OrderRepository orderRepository, InventoryService inventoryService, Clock clock) {
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.clock = clock;
    }

    @Override
    public OrderRecord createOrder(String customerName, Long itemId, int quantity) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        InventoryItem item = inventoryService.getItemById(itemId);
        inventoryService.reserveStock(itemId, quantity);

        BigDecimal totalAmount = item.price().multiply(BigDecimal.valueOf(quantity));
        OrderRecord orderRecord = new OrderRecord(null, customerName.trim(), itemId, quantity, totalAmount,
                OrderStatus.CREATED, Instant.now(clock));
        return orderRepository.save(orderRecord);
    }

    @Override
    public OrderRecord getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el recurso solicitado"));
    }

    @Override
    public List<OrderRecord> listOrders() {
        return orderRepository.findAll();
    }
}
