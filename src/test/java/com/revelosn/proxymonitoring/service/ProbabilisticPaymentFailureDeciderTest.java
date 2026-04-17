package com.revelosn.proxymonitoring.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ProbabilisticPaymentFailureDeciderTest {

    @Test
    void defaultConstructorShouldFallbackWhenJvmDefaultRandomGeneratorIsUnavailable() {
        try (MockedStatic<RandomGenerator> randomGeneratorMock = Mockito.mockStatic(RandomGenerator.class)) {
            randomGeneratorMock.when(RandomGenerator::getDefault)
                    .thenThrow(new IllegalArgumentException("Unsupported default random"));

            assertDoesNotThrow(() -> {
                new ProbabilisticPaymentFailureDecider();
            });
        }
    }
}
