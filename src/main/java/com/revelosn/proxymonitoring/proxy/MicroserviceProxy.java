package com.revelosn.proxymonitoring.proxy;

public interface MicroserviceProxy<T> {

    T execute(String operation, Object... params);
}

