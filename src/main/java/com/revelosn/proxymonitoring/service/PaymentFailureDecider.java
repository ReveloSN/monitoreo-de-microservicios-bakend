package com.revelosn.proxymonitoring.service;

@FunctionalInterface
public interface PaymentFailureDecider {

    boolean shouldFail();
}

