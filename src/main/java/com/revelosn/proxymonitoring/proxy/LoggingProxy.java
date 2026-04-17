package com.revelosn.proxymonitoring.proxy;

import com.revelosn.proxymonitoring.model.AuditLogEntry;
import com.revelosn.proxymonitoring.model.AuditStatus;
import com.revelosn.proxymonitoring.repository.AuditLogRepository;
import com.revelosn.proxymonitoring.util.OperationParameterResolver;
import com.revelosn.proxymonitoring.util.RequestContextUtils;
import com.revelosn.proxymonitoring.util.StackTraceUtils;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class LoggingProxy<T> implements MicroserviceProxy<ProxyExecutionResult<T>> {

    private final ServiceOperationExecutor<T> delegate;
    private final AuditLogRepository auditLogRepository;
    private final Clock clock;

    public LoggingProxy(ServiceOperationExecutor<T> delegate, AuditLogRepository auditLogRepository, Clock clock) {
        this.delegate = delegate;
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
    }

    @Override
    public ProxyExecutionResult<T> execute(String operation, Object... params) {
        String requestId = UUID.randomUUID().toString();
        Map<String, Object> inputParams = OperationParameterResolver.asMap(params);
        Instant timestamp = Instant.now(clock);
        long startedAt = System.nanoTime();

        RequestContextUtils.storeRequestId(requestId);

        try {
            T payload = delegate.executeOperation(operation, params);
            long durationMs = Math.max(0, (System.nanoTime() - startedAt) / 1_000_000);
            auditLogRepository.save(new AuditLogEntry(delegate.getServiceId(), operation, durationMs, AuditStatus.SUCCESS,
                    requestId, timestamp, inputParams, payload, null, null, RequestContextUtils.currentPath()));
            return new ProxyExecutionResult<>(requestId, payload);
        } catch (RuntimeException exception) {
            long durationMs = Math.max(0, (System.nanoTime() - startedAt) / 1_000_000);
            auditLogRepository.save(new AuditLogEntry(delegate.getServiceId(), operation, durationMs, AuditStatus.ERROR,
                    requestId, timestamp, inputParams, null, exception.getMessage(),
                    StackTraceUtils.summarize(exception), RequestContextUtils.currentPath()));
            throw exception;
        }
    }
}
