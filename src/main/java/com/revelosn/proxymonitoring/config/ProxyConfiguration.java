package com.revelosn.proxymonitoring.config;

import com.revelosn.proxymonitoring.proxy.InventoryOperationExecutor;
import com.revelosn.proxymonitoring.proxy.LoggingProxy;
import com.revelosn.proxymonitoring.proxy.MicroserviceProxy;
import com.revelosn.proxymonitoring.proxy.OrderOperationExecutor;
import com.revelosn.proxymonitoring.proxy.PaymentOperationExecutor;
import com.revelosn.proxymonitoring.proxy.ProxyExecutionResult;
import com.revelosn.proxymonitoring.repository.AuditLogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ProxyConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean("inventoryProxy")
    public MicroserviceProxy<ProxyExecutionResult<Object>> inventoryProxy(InventoryOperationExecutor executor,
                                                                          AuditLogRepository auditLogRepository,
                                                                          Clock clock) {
        return new LoggingProxy<>(executor, auditLogRepository, clock);
    }

    @Bean("ordersProxy")
    public MicroserviceProxy<ProxyExecutionResult<Object>> ordersProxy(OrderOperationExecutor executor,
                                                                       AuditLogRepository auditLogRepository,
                                                                       Clock clock) {
        return new LoggingProxy<>(executor, auditLogRepository, clock);
    }

    @Bean("paymentsProxy")
    public MicroserviceProxy<ProxyExecutionResult<Object>> paymentsProxy(PaymentOperationExecutor executor,
                                                                         AuditLogRepository auditLogRepository,
                                                                         Clock clock) {
        return new LoggingProxy<>(executor, auditLogRepository, clock);
    }
}
