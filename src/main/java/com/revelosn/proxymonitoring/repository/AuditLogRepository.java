package com.revelosn.proxymonitoring.repository;

import com.revelosn.proxymonitoring.model.AuditLogEntry;

import java.util.List;

public interface AuditLogRepository {

    void save(AuditLogEntry entry);

    List<AuditLogEntry> findAll();
}

