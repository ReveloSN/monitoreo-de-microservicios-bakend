package com.revelosn.proxymonitoring.metrics;

import com.revelosn.proxymonitoring.dto.MetricsSummaryResponse;
import com.revelosn.proxymonitoring.dto.ServiceMetricsResponse;
import com.revelosn.proxymonitoring.model.AuditLogEntry;
import com.revelosn.proxymonitoring.model.AuditStatus;
import com.revelosn.proxymonitoring.repository.InMemoryAuditLogRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetricsServiceTest {

    @Test
    void getSummaryShouldAggregateTotalsErrorRateAndServiceBreakdown() {
        InMemoryAuditLogRepository repository = new InMemoryAuditLogRepository();
        repository.save(new AuditLogEntry("inventory", "listItems", 100, AuditStatus.SUCCESS, "req-1",
                Instant.parse("2026-04-17T18:00:00Z"), Map.of(), Map.of("size", 4), null, null, "/api/services/inventory/listItems"));
        repository.save(new AuditLogEntry("orders", "createOrder", 200, AuditStatus.SUCCESS, "req-2",
                Instant.parse("2026-04-17T18:01:00Z"), Map.of("quantity", 2), Map.of("id", 1), null, null, "/api/services/orders/createOrder"));
        repository.save(new AuditLogEntry("payments", "processPayment", 300, AuditStatus.ERROR, "req-3",
                Instant.parse("2026-04-17T18:02:00Z"), Map.of("amount", 40), null,
                "La operación de pago falló de forma intencional para la simulación", "stack", "/api/services/payments/processPayment"));
        repository.save(new AuditLogEntry("payments", "getPaymentById", 150, AuditStatus.SUCCESS, "req-4",
                Instant.parse("2026-04-17T18:03:00Z"), Map.of("paymentId", 10), Map.of("id", 10), null, null, "/api/services/payments/getPaymentById"));

        MetricsService service = new MetricsService(repository);

        MetricsSummaryResponse summary = service.getSummary();

        assertEquals(4, summary.totalCalls());
        assertEquals(0.25d, summary.errorRate());
        assertEquals(187.5d, summary.averageResponseTime());
        assertEquals(3, summary.services().size());

        ServiceMetricsResponse paymentMetrics = summary.services().stream()
                .filter(serviceMetrics -> serviceMetrics.serviceId().equals("payments"))
                .findFirst()
                .orElseThrow();

        assertEquals(2, paymentMetrics.totalCalls());
        assertEquals(0.5d, paymentMetrics.errorRate());
        assertTrue(paymentMetrics.hasIssues());
        assertEquals(2, paymentMetrics.recentCalls().size());
    }
}

