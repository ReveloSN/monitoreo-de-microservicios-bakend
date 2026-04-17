package com.revelosn.proxymonitoring.metrics;

import com.revelosn.proxymonitoring.dto.MetricsSummaryResponse;
import com.revelosn.proxymonitoring.dto.PagedResponse;
import com.revelosn.proxymonitoring.dto.RecentCallResponse;
import com.revelosn.proxymonitoring.dto.ServiceMetricsResponse;
import com.revelosn.proxymonitoring.model.AuditLogEntry;
import com.revelosn.proxymonitoring.model.AuditStatus;
import com.revelosn.proxymonitoring.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final AuditLogRepository auditLogRepository;

    public MetricsService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public MetricsSummaryResponse getSummary() {
        List<AuditLogEntry> logs = sortedLogs();
        return new MetricsSummaryResponse(
                logs.size(),
                calculateErrorRate(logs),
                calculateAverageDuration(logs),
                buildServiceMetrics(logs)
        );
    }

    public PagedResponse<AuditLogEntry> getLogs(String serviceId,
                                                AuditStatus status,
                                                Instant from,
                                                Instant to,
                                                int page,
                                                int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        List<AuditLogEntry> filteredLogs = sortedLogs().stream()
                .filter(matchesService(serviceId))
                .filter(matchesStatus(status))
                .filter(matchesDateRange(from, to))
                .toList();

        int fromIndex = Math.min(normalizedPage * normalizedSize, filteredLogs.size());
        int toIndex = Math.min(fromIndex + normalizedSize, filteredLogs.size());
        return PagedResponse.of(filteredLogs.subList(fromIndex, toIndex), normalizedPage, normalizedSize, filteredLogs.size());
    }

    private List<AuditLogEntry> sortedLogs() {
        return auditLogRepository.findAll().stream()
                .sorted(Comparator.comparing(AuditLogEntry::timestamp).reversed())
                .toList();
    }

    private Predicate<AuditLogEntry> matchesService(String serviceId) {
        if (serviceId == null || serviceId.isBlank()) {
            return entry -> true;
        }
        return entry -> entry.serviceId().equalsIgnoreCase(serviceId);
    }

    private Predicate<AuditLogEntry> matchesStatus(AuditStatus status) {
        if (status == null) {
            return entry -> true;
        }
        return entry -> entry.status() == status;
    }

    private Predicate<AuditLogEntry> matchesDateRange(Instant from, Instant to) {
        return entry -> (from == null || !entry.timestamp().isBefore(from))
                && (to == null || !entry.timestamp().isAfter(to));
    }

    private List<ServiceMetricsResponse> buildServiceMetrics(List<AuditLogEntry> logs) {
        return logs.stream()
                .collect(Collectors.groupingBy(AuditLogEntry::serviceId))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> buildServiceMetrics(entry.getKey(), entry.getValue()))
                .toList();
    }

    private ServiceMetricsResponse buildServiceMetrics(String serviceId, List<AuditLogEntry> logs) {
        double errorRate = calculateErrorRate(logs);
        double successRate = logs.isEmpty() ? 0.0d : 1.0d - errorRate;
        return new ServiceMetricsResponse(
                serviceId,
                logs.size(),
                successRate,
                errorRate,
                calculateAverageDuration(logs),
                errorRate > 0.15d,
                logs.stream()
                        .sorted(Comparator.comparing(AuditLogEntry::timestamp).reversed())
                        .limit(20)
                        .map(log -> new RecentCallResponse(log.requestId(), log.timestamp(), log.durationMs(),
                                log.status().name(), log.operation()))
                        .toList()
        );
    }

    private double calculateErrorRate(List<AuditLogEntry> logs) {
        if (logs.isEmpty()) {
            return 0.0d;
        }
        long errors = logs.stream()
                .filter(log -> log.status() == AuditStatus.ERROR)
                .count();
        return (double) errors / logs.size();
    }

    private double calculateAverageDuration(List<AuditLogEntry> logs) {
        if (logs.isEmpty()) {
            return 0.0d;
        }
        return logs.stream()
                .filter(Objects::nonNull)
                .mapToLong(AuditLogEntry::durationMs)
                .average()
                .orElse(0.0d);
    }
}
