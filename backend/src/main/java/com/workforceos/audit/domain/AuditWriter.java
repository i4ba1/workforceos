package com.workforceos.audit.domain;

/** Write-side port for the immutable audit stream. */
public interface AuditWriter {

    void append(AuditEvent event);
}
