package com.revelosn.proxymonitoring.repository;

import com.revelosn.proxymonitoring.model.InventoryItem;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {

    List<InventoryItem> findAll();

    Optional<InventoryItem> findById(Long id);

    InventoryItem save(InventoryItem item);

    InventoryItem reserveStock(Long id, int quantity);
}

