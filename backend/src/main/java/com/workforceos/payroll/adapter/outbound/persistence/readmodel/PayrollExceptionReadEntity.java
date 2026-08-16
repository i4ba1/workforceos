package com.workforceos.payroll.adapter.outbound.persistence.readmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

/** Read-only projection of the attendance module's {@code attendance_exception} table. */
@Entity
@Immutable
@Table(name = "attendance_exception")
public class PayrollExceptionReadEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "record_id")
    private UUID recordId;

    @Column(name = "state")
    private String state;

    protected PayrollExceptionReadEntity() {
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getRecordId() {
        return recordId;
    }

    public String getState() {
        return state;
    }
}
