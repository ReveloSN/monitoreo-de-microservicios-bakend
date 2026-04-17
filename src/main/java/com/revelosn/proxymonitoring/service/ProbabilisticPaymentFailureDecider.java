package com.revelosn.proxymonitoring.service;

import com.revelosn.proxymonitoring.util.CompatibleRandomGeneratorFactory;
import org.springframework.stereotype.Component;

import java.util.random.RandomGenerator;

@Component
public class ProbabilisticPaymentFailureDecider implements PaymentFailureDecider {

    private static final double FAILURE_RATE = 0.10d;

    private final RandomGenerator randomGenerator;

    public ProbabilisticPaymentFailureDecider() {
        this(CompatibleRandomGeneratorFactory.create());
    }

    ProbabilisticPaymentFailureDecider(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    @Override
    public boolean shouldFail() {
        return randomGenerator.nextDouble() < FAILURE_RATE;
    }
}
