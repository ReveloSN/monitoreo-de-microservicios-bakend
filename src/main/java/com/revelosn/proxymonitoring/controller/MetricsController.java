package com.revelosn.proxymonitoring.controller;

import com.revelosn.proxymonitoring.dto.ApiResponse;
import com.revelosn.proxymonitoring.metrics.LoadSimulationService;
import com.revelosn.proxymonitoring.metrics.MetricsService;
import com.revelosn.proxymonitoring.model.AuditStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;
    private final LoadSimulationService loadSimulationService;

    public MetricsController(MetricsService metricsService, LoadSimulationService loadSimulationService) {
        this.metricsService = metricsService;
        this.loadSimulationService = loadSimulationService;
    }

    @GetMapping("/summary")
    public ApiResponse<?> getSummary() {
        return ApiResponse.success("Resumen de métricas obtenido correctamente", null, metricsService.getSummary());
    }

    @GetMapping("/logs")
    public ApiResponse<?> getLogs(@RequestParam(required = false) String service,
                                  @RequestParam(required = false) AuditStatus status,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success("Logs obtenidos correctamente", null,
                metricsService.getLogs(service, status, from, to, page, size));
    }

    @PostMapping("/simulate-load")
    public ApiResponse<?> simulateLoad() {
        return ApiResponse.success("Se generaron 50 llamadas de prueba", null,
                loadSimulationService.simulateLoad(50));
    }
}
