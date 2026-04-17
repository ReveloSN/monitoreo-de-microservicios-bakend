package com.revelosn.proxymonitoring.service;

import com.revelosn.proxymonitoring.model.InventoryItem;

import java.util.List;

public interface InventoryService {

    List<InventoryItem> listItems();

    InventoryItem getItemById(Long itemId);

    InventoryItem reserveStock(Long itemId, int quantity);
}

