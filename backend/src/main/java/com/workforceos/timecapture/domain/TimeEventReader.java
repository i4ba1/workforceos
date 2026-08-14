package com.workforceos.timecapture.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.TimeEventId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Read-side port for immutable raw time events and idempotency records. */
public interface TimeEventReader {

    Optional<TimeEvent> findById(TenantId tenantId, TimeEventId id);

    Optional<TimeEvent> findBySource(TenantId tenantId, EventSource source);

    List<TimeEvent> findForEmployee(TenantId tenantId, EmployeeId employeeId, Instant from, Instant to);

    Optional<IngestionRecord> findIngestion(TenantId tenantId, String idempotencyKey);
}
