package com.revelosn.proxymonitoring.service;

import com.revelosn.proxymonitoring.model.OrderRecord;

import java.util.List;

public interface OrderService {

    OrderRecord createOrder(String customerName, Long itemId, int quantity);

    OrderRecord getOrderById(Long orderId);

    List<OrderRecord> listOrders();
}

