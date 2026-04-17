package com.revelosn.proxymonitoring.metrics;

import com.revelosn.proxymonitoring.dto.SimulationResponse;
import com.revelosn.proxymonitoring.dto.SimulationServiceResult;
import com.revelosn.proxymonitoring.model.InventoryItem;
import com.revelosn.proxymonitoring.model.OrderRecord;
import com.revelosn.proxymonitoring.model.PaymentRecord;
import com.revelosn.proxymonitoring.proxy.MicroserviceProxy;
import com.revelosn.proxymonitoring.proxy.ProxyExecutionResult;
import com.revelosn.proxymonitoring.repository.InventoryRepository;
import com.revelosn.proxymonitoring.repository.OrderRepository;
import com.revelosn.proxymonitoring.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

@Service
public class LoadSimulationService {

    private final MicroserviceProxy<ProxyExecutionResult<Object>> inventoryProxy;
    private final MicroserviceProxy<ProxyExecutionResult<Object>> ordersProxy;
    private final MicroserviceProxy<ProxyExecutionResult<Object>> paymentsProxy;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final Clock clock;
    private final RandomGenerator randomGenerator = RandomGenerator.getDefault();

    public LoadSimulationService(@Qualifier("inventoryProxy") MicroserviceProxy<ProxyExecutionResult<Object>> inventoryProxy,
                                 @Qualifier("ordersProxy") MicroserviceProxy<ProxyExecutionResult<Object>> ordersProxy,
                                 @Qualifier("paymentsProxy") MicroserviceProxy<ProxyExecutionResult<Object>> paymentsProxy,
                                 InventoryRepository inventoryRepository,
                                 OrderRepository orderRepository,
                                 PaymentRepository paymentRepository,
                                 Clock clock) {
        this.inventoryProxy = inventoryProxy;
        this.ordersProxy = ordersProxy;
        this.paymentsProxy = paymentsProxy;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.clock = clock;
    }

    public SimulationResponse simulateLoad(int totalCalls) {
        Instant start = Instant.now(clock);
        Map<String, ServiceCounter> counters = new LinkedHashMap<>();
        counters.put("inventory", new ServiceCounter());
        counters.put("orders", new ServiceCounter());
        counters.put("payments", new ServiceCounter());

        int successfulCalls = 0;
        int failedCalls = 0;

        for (int index = 0; index < totalCalls; index++) {
            String serviceId = pickServiceId();
            ServiceCounter counter = counters.get(serviceId);
            counter.totalCalls++;
            try {
                executeRandomOperation(serviceId);
                counter.successCalls++;
                successfulCalls++;
            } catch (RuntimeException exception) {
                counter.errorCalls++;
                failedCalls++;
            }
        }

        long durationMs = Math.max(0, Instant.now(clock).toEpochMilli() - start.toEpochMilli());
        List<SimulationServiceResult> results = counters.entrySet().stream()
                .map(entry -> new SimulationServiceResult(entry.getKey(), entry.getValue().totalCalls,
                        entry.getValue().successCalls, entry.getValue().errorCalls))
                .toList();

        return new SimulationResponse(totalCalls, successfulCalls, failedCalls, durationMs, results);
    }

    private String pickServiceId() {
        return switch (randomGenerator.nextInt(3)) {
            case 0 -> "inventory";
            case 1 -> "orders";
            default -> "payments";
        };
    }

    private void executeRandomOperation(String serviceId) {
        switch (serviceId) {
            case "inventory" -> executeInventoryOperation();
            case "orders" -> executeOrderOperation();
            default -> executePaymentOperation();
        }
    }

    private void executeInventoryOperation() {
        List<InventoryItem> items = inventoryRepository.findAll();
        InventoryItem selectedItem = items.get(randomGenerator.nextInt(items.size()));
        switch (randomGenerator.nextInt(3)) {
            case 0 -> inventoryProxy.execute("listItems", Map.of());
            case 1 -> inventoryProxy.execute("getItemById", Map.of("itemId", selectedItem.id()));
            default -> inventoryProxy.execute("reserveStock", Map.of("itemId", selectedItem.id(), "quantity", 1));
        }
    }

    private void executeOrderOperation() {
        List<OrderRecord> orders = orderRepository.findAll();
        switch (randomGenerator.nextInt(3)) {
            case 0 -> ordersProxy.execute("listOrders", Map.of());
            case 1 -> {
                if (orders.isEmpty()) {
                    createRandomOrder();
                } else {
                    OrderRecord order = orders.get(randomGenerator.nextInt(orders.size()));
                    ordersProxy.execute("getOrderById", Map.of("orderId", order.id()));
                }
            }
            default -> createRandomOrder();
        }
    }

    private void executePaymentOperation() {
        List<OrderRecord> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            createRandomOrder();
            orders = orderRepository.findAll();
        }
        switch (randomGenerator.nextInt(2)) {
            case 0 -> {
                OrderRecord order = orders.get(randomGenerator.nextInt(orders.size()));
                paymentsProxy.execute("processPayment", Map.of(
                        "orderId", order.id(),
                        "amount", order.totalAmount(),
                        "paymentMethod", randomGenerator.nextBoolean() ? "CARD" : "TRANSFER"
                ));
            }
            default -> {
                List<PaymentRecord> payments = paymentRepository.findAll();
                if (payments.isEmpty()) {
                    OrderRecord order = orders.get(randomGenerator.nextInt(orders.size()));
                    paymentsProxy.execute("processPayment", Map.of(
                            "orderId", order.id(),
                            "amount", order.totalAmount(),
                            "paymentMethod", "CARD"
                    ));
                } else {
                    PaymentRecord payment = payments.get(randomGenerator.nextInt(payments.size()));
                    paymentsProxy.execute("getPaymentById", Map.of("paymentId", payment.id()));
                }
            }
        }
    }

    private void createRandomOrder() {
        List<InventoryItem> items = inventoryRepository.findAll().stream()
                .filter(item -> item.availableStock() > 0)
                .toList();
        InventoryItem item = items.get(randomGenerator.nextInt(items.size()));
        ordersProxy.execute("createOrder", Map.of(
                "customerName", "Cliente Demo " + randomGenerator.nextInt(100, 999),
                "itemId", item.id(),
                "quantity", 1
        ));
    }

    private static final class ServiceCounter {
        private int totalCalls;
        private int successCalls;
        private int errorCalls;
    }
}
