package com.revelosn.proxymonitoring.metrics;

import com.revelosn.proxymonitoring.proxy.MicroserviceProxy;
import com.revelosn.proxymonitoring.proxy.ProxyExecutionResult;
import com.revelosn.proxymonitoring.repository.InMemoryInventoryRepository;
import com.revelosn.proxymonitoring.repository.InMemoryOrderRepository;
import com.revelosn.proxymonitoring.repository.InMemoryPaymentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LoadSimulationServiceTest {

    @Test
    void constructorShouldFallbackWhenJvmDefaultRandomGeneratorIsUnavailable() {
        MicroserviceProxy<ProxyExecutionResult<Object>> proxy = (operation, params) -> new ProxyExecutionResult<>("req", null);

        try (MockedStatic<RandomGenerator> randomGeneratorMock = Mockito.mockStatic(RandomGenerator.class)) {
            randomGeneratorMock.when(RandomGenerator::getDefault)
                    .thenThrow(new IllegalArgumentException("Unsupported default random"));

            assertDoesNotThrow(() -> {
                new LoadSimulationService(
                        proxy,
                        proxy,
                        proxy,
                        new InMemoryInventoryRepository(),
                        new InMemoryOrderRepository(),
                        new InMemoryPaymentRepository(),
                        Clock.fixed(Instant.parse("2026-04-17T19:00:00Z"), ZoneOffset.UTC)
                );
            });
        }
    }
}
