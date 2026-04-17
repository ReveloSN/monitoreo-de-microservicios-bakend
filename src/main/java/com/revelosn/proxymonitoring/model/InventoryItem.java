package com.revelosn.proxymonitoring.model;

import java.math.BigDecimal;

public record InventoryItem(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int availableStock
) {

    public InventoryItem withAvailableStock(int newStock) {
        return new InventoryItem(id, name, description, price, newStock);
    }
}

