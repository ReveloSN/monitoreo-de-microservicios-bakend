package com.revelosn.proxymonitoring.controller;

import com.revelosn.proxymonitoring.dto.ApiResponse;
import com.revelosn.proxymonitoring.dto.ServiceOperationRequest;
import com.revelosn.proxymonitoring.proxy.MicroserviceProxy;
import com.revelosn.proxymonitoring.proxy.ProxyExecutionResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
public class ServiceExecutionController {

    private final MicroserviceProxy<ProxyExecutionResult<Object>> inventoryProxy;
    private final MicroserviceProxy<ProxyExecutionResult<Object>> ordersProxy;
    private final MicroserviceProxy<ProxyExecutionResult<Object>> paymentsProxy;

    public ServiceExecutionController(@Qualifier("inventoryProxy") MicroserviceProxy<ProxyExecutionResult<Object>> inventoryProxy,
                                      @Qualifier("ordersProxy") MicroserviceProxy<ProxyExecutionResult<Object>> ordersProxy,
                                      @Qualifier("paymentsProxy") MicroserviceProxy<ProxyExecutionResult<Object>> paymentsProxy) {
        this.inventoryProxy = inventoryProxy;
        this.ordersProxy = ordersProxy;
        this.paymentsProxy = paymentsProxy;
    }

    @PostMapping("/inventory/{operation}")
    public ApiResponse<Object> executeInventoryOperation(@PathVariable String operation,
                                                         @Valid @RequestBody ServiceOperationRequest request) {
        return successResponse(inventoryProxy.execute(operation, request.params()));
    }

    @PostMapping("/orders/{operation}")
    public ApiResponse<Object> executeOrdersOperation(@PathVariable String operation,
                                                      @Valid @RequestBody ServiceOperationRequest request) {
        return successResponse(ordersProxy.execute(operation, request.params()));
    }

    @PostMapping("/payments/{operation}")
    public ApiResponse<Object> executePaymentsOperation(@PathVariable String operation,
                                                        @Valid @RequestBody ServiceOperationRequest request) {
        return successResponse(paymentsProxy.execute(operation, request.params()));
    }

    private ApiResponse<Object> successResponse(ProxyExecutionResult<Object> result) {
        return ApiResponse.success("Operación ejecutada correctamente", result.requestId(), result.payload());
    }
}

