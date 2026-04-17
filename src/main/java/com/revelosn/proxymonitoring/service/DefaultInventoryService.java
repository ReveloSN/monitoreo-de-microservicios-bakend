package com.revelosn.proxymonitoring.service;

import com.revelosn.proxymonitoring.exception.ResourceNotFoundException;
import com.revelosn.proxymonitoring.model.InventoryItem;
import com.revelosn.proxymonitoring.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultInventoryService implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public DefaultInventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<InventoryItem> listItems() {
        return inventoryRepository.findAll();
    }

    @Override
    public InventoryItem getItemById(Long itemId) {
        return inventoryRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el recurso solicitado"));
    }

    @Override
    public InventoryItem reserveStock(Long itemId, int quantity) {
        return inventoryRepository.reserveStock(itemId, quantity);
    }
}

