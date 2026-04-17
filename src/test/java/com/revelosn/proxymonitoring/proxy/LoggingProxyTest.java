package com.revelosn.proxymonitoring.proxy;

import com.revelosn.proxymonitoring.model.AuditLogEntry;
import com.revelosn.proxymonitoring.model.AuditStatus;
import com.revelosn.proxymonitoring.repository.InMemoryAuditLogRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingProxyTest {

    @Test
    void executeShouldStoreSuccessAuditLogAndReturnPayload() {
        InMemoryAuditLogRepository repository = new InMemoryAuditLogRepository();
        Clock clock = Clock.fixed(Instant.parse("2026-04-17T19:00:00Z"), ZoneOffset.UTC);

        ServiceOperationExecutor<Object> executor = new ServiceOperationExecutor<>() {
            @Override
            public String getServiceId() {
                return "inventory";
            }

            @Override
            public Object executeOperation(String operation, Object... params) {
                return Map.of("operation", operation, "count", 4);
            }
        };

        LoggingProxy<Object> proxy = new LoggingProxy<>(executor, repository, clock);

        ProxyExecutionResult<Object> result = proxy.execute("listItems", Map.of("page", 0));

        assertNotNull(result.requestId());
        assertEquals(Map.of("operation", "listItems", "count", 4), result.payload());

        List<AuditLogEntry> entries = repository.findAll();
        assertEquals(1, entries.size());

        AuditLogEntry entry = entries.getFirst();
        assertEquals("inventory", entry.serviceId());
        assertEquals("listItems", entry.operation());
        assertEquals(AuditStatus.SUCCESS, entry.status());
        assertEquals("SUCCESS", entry.status().name());
        assertEquals(Instant.parse("2026-04-17T19:00:00Z"), entry.timestamp());
        assertEquals(Map.of("page", 0), entry.inputParams());
        assertEquals(result.payload(), entry.response());
        assertEquals(result.requestId(), entry.requestId());
        assertTrue(entry.durationMs() >= 0);
    }

    @Test
    void executeShouldStoreErrorAuditLogAndPropagateException() {
        InMemoryAuditLogRepository repository = new InMemoryAuditLogRepository();
        Clock clock = Clock.fixed(Instant.parse("2026-04-17T19:00:00Z"), ZoneOffset.UTC);

        ServiceOperationExecutor<Object> executor = new ServiceOperationExecutor<>() {
            @Override
            public String getServiceId() {
                return "payments";
            }

            @Override
            public Object executeOperation(String operation, Object... params) {
                throw new IllegalStateException("Simulated crash");
            }
        };

        LoggingProxy<Object> proxy = new LoggingProxy<>(executor, repository, clock);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> proxy.execute("processPayment", Map.of("amount", 15)));

        assertEquals("Simulated crash", exception.getMessage());

        List<AuditLogEntry> entries = repository.findAll();
        assertEquals(1, entries.size());

        AuditLogEntry entry = entries.getFirst();
        assertEquals("payments", entry.serviceId());
        assertEquals("processPayment", entry.operation());
        assertEquals(AuditStatus.ERROR, entry.status());
        assertEquals("Simulated crash", entry.errorMessage());
        assertTrue(entry.stackTrace().contains("IllegalStateException"));
        assertEquals(Map.of("amount", 15), entry.inputParams());
        assertNotNull(entry.requestId());
    }
}

