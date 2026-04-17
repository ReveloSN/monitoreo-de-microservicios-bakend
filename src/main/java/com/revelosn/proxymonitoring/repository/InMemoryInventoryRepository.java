package com.revelosn.proxymonitoring.repository;

import com.revelosn.proxymonitoring.exception.InsufficientStockException;
import com.revelosn.proxymonitoring.exception.ResourceNotFoundException;
import com.revelosn.proxymonitoring.model.InventoryItem;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryInventoryRepository implements InventoryRepository {

    private final ConcurrentHashMap<Long, InventoryItem> items = new ConcurrentHashMap<>();

    @Override
    public List<InventoryItem> findAll() {
        return items.values().stream()
                .sorted(Comparator.comparing(InventoryItem::id))
                .toList();
    }

    @Override
    public Optional<InventoryItem> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public InventoryItem save(InventoryItem item) {
        items.put(item.id(), item);
        return item;
    }

    @Override
    public InventoryItem reserveStock(Long id, int quantity) {
        return items.compute(id, (key, existing) -> {
            if (existing == null) {
                throw new ResourceNotFoundException("No se encontró el recurso solicitado");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            if (existing.availableStock() < quantity) {
                throw new InsufficientStockException("No hay stock suficiente para completar la operación");
            }
            return existing.withAvailableStock(existing.availableStock() - quantity);
        });
    }
}

