package com.revelosn.proxymonitoring.proxy;

public record ProxyExecutionResult<T>(
        String requestId,
        T payload
) {
}

