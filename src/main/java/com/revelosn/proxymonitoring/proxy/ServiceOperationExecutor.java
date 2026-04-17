package com.revelosn.proxymonitoring.proxy;

public interface ServiceOperationExecutor<T> {

    String getServiceId();

    T executeOperation(String operation, Object... params);
}

