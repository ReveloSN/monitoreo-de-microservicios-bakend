package com.revelosn.proxymonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ProxyMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyMonitoringApplication.class, args);
    }
}
