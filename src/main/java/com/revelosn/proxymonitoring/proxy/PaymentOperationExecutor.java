package com.revelosn.proxymonitoring.proxy;

import com.revelosn.proxymonitoring.exception.UnsupportedServiceOperationException;
import com.revelosn.proxymonitoring.service.PaymentService;
import com.revelosn.proxymonitoring.util.OperationParameterResolver;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentOperationExecutor implements ServiceOperationExecutor<Object> {

    private final PaymentService paymentService;

    public PaymentOperationExecutor(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public String getServiceId() {
        return "payments";
    }

    @Override
    public Object executeOperation(String operation, Object... params) {
        Map<String, Object> parameterMap = OperationParameterResolver.asMap(params);
        return switch (operation) {
            case "processPayment" -> paymentService.processPayment(
                    OperationParameterResolver.requireLong(parameterMap, "orderId", "id"),
                    OperationParameterResolver.requireBigDecimal(parameterMap, "amount"),
                    OperationParameterResolver.requireString(parameterMap, "paymentMethod"));
            case "getPaymentById" -> paymentService.getPaymentById(
                    OperationParameterResolver.requireLong(parameterMap, "paymentId", "id"));
            default -> throw new UnsupportedServiceOperationException("La operación solicitada no está soportada");
        };
    }
}

