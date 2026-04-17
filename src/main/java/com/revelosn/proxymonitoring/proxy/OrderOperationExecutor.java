package com.revelosn.proxymonitoring.proxy;

import com.revelosn.proxymonitoring.exception.UnsupportedServiceOperationException;
import com.revelosn.proxymonitoring.service.OrderService;
import com.revelosn.proxymonitoring.util.OperationParameterResolver;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderOperationExecutor implements ServiceOperationExecutor<Object> {

    private final OrderService orderService;

    public OrderOperationExecutor(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public String getServiceId() {
        return "orders";
    }

    @Override
    public Object executeOperation(String operation, Object... params) {
        Map<String, Object> parameterMap = OperationParameterResolver.asMap(params);
        return switch (operation) {
            case "createOrder" -> orderService.createOrder(
                    OperationParameterResolver.requireString(parameterMap, "customerName"),
                    OperationParameterResolver.requireLong(parameterMap, "itemId", "id"),
                    OperationParameterResolver.requireInt(parameterMap, "quantity"));
            case "getOrderById" -> orderService.getOrderById(
                    OperationParameterResolver.requireLong(parameterMap, "orderId", "id"));
            case "listOrders" -> orderService.listOrders();
            default -> throw new UnsupportedServiceOperationException("La operación solicitada no está soportada");
        };
    }
}

