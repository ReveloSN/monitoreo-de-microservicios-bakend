package com.revelosn.proxymonitoring.proxy;

import com.revelosn.proxymonitoring.exception.UnsupportedServiceOperationException;
import com.revelosn.proxymonitoring.service.InventoryService;
import com.revelosn.proxymonitoring.util.OperationParameterResolver;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InventoryOperationExecutor implements ServiceOperationExecutor<Object> {

    private final InventoryService inventoryService;

    public InventoryOperationExecutor(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public String getServiceId() {
        return "inventory";
    }

    @Override
    public Object executeOperation(String operation, Object... params) {
        Map<String, Object> parameterMap = OperationParameterResolver.asMap(params);
        return switch (operation) {
            case "listItems" -> inventoryService.listItems();
            case "getItemById" -> inventoryService.getItemById(
                    OperationParameterResolver.requireLong(parameterMap, "itemId", "id"));
            case "reserveStock" -> inventoryService.reserveStock(
                    OperationParameterResolver.requireLong(parameterMap, "itemId", "id"),
                    OperationParameterResolver.requireInt(parameterMap, "quantity"));
            default -> throw new UnsupportedServiceOperationException("La operación solicitada no está soportada");
        };
    }
}

