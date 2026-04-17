package com.revelosn.proxymonitoring.repository;

import com.revelosn.proxymonitoring.model.AuditLogEntry;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class InMemoryAuditLogRepository implements AuditLogRepository {

    private final ConcurrentLinkedDeque<AuditLogEntry> entries = new ConcurrentLinkedDeque<>();

    @Override
    public void save(AuditLogEntry entry) {
        entries.addFirst(entry);
    }

    @Override
    public List<AuditLogEntry> findAll() {
        return new ArrayList<>(entries);
    }
}

