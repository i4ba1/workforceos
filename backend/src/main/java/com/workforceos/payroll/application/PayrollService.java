package com.workforceos.payroll.application;

import com.workforceos.audit.domain.AuditEvent;
import com.workforceos.audit.domain.AuditWriter;
import com.workforceos.payroll.domain.PayPeriod;
import com.workforceos.payroll.domain.PayPeriodState;
import com.workforceos.payroll.domain.PayrollAttendanceLine;
import com.workforceos.payroll.domain.PayrollDataSource;
import com.workforceos.payroll.domain.PayrollExporter;
import com.workforceos.payroll.domain.PayrollExport;
import com.workforceos.payroll.domain.PayrollProjection;
import com.workforceos.payroll.domain.PayrollReadiness;
import com.workforceos.payroll.domain.PayrollStore;
import com.workforceos.payroll.domain.event.PayPeriodClosed;
import com.workforceos.payroll.domain.event.PayrollExportGenerated;
import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.error.NotFoundException;
import com.workforceos.shared.id.AuditEventId;
import com.workforceos.shared.id.PayPeriodId;
import com.workforceos.shared.id.PayrollExportId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import com.workforceos.shared.time.Minutes;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/**
 * Payroll period lifecycle: open, readiness, close/reopen with audit, and deterministic
 * CSV export with checksum/version.
 */
@Service
public class PayrollService {

    private final PayrollStore store;
    private final PayrollDataSource dataSource;
    private final PayrollExporter exporter;
    private final AuditWriter auditWriter;
    private final ApplicationEventPublisher eventPublisher;

    public PayrollService(PayrollStore store, PayrollDataSource dataSource, PayrollExporter exporter,
                          AuditWriter auditWriter, ApplicationEventPublisher eventPublisher) {
        this.store = store;
        this.dataSource = dataSource;
        this.exporter = exporter;
        this.auditWriter = auditWriter;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PayPeriod open(TenantId tenantId, LocalDate startDate, LocalDate endDate) {
        return store.savePeriod(new PayPeriod(PayPeriodId.newId(), tenantId, startDate, endDate));
    }

    @Transactional(readOnly = true)
    public List<PayPeriod> list(TenantId tenantId) {
        return store.findPeriods(tenantId);
    }

    @Transactional(readOnly = true)
    public PayPeriod get(TenantId tenantId, PayPeriodId id) {
        return store.findPeriod(tenantId, id)
                .orElseThrow(() -> new NotFoundException("pay_period.not_found", "Pay period not found: " + id));
    }

    @Transactional(readOnly = true)
    public PayrollReadiness readiness(TenantId tenantId, PayPeriodId id) {
        PayPeriod period = get(tenantId, id);
        return summarize(dataSource.findAttendance(tenantId, period.startDate(), period.endDate()));
    }

    @Transactional
    public PayPeriod close(TenantId tenantId, PayPeriodId id, UserId by) {
        PayPeriod period = get(tenantId, id);
        List<PayrollAttendanceLine> lines = dataSource.findAttendance(tenantId, period.startDate(), period.endDate());
        long unresolved = lines.stream().filter(PayrollAttendanceLine::hasOpenException).count();
        if (unresolved > 0) {
            throw new ConflictException("payroll.unresolved",
                    "Cannot close period: " + unresolved + " unresolved record(s)");
        }
        period.startValidation();
        period.close(by, Instant.now());
        PayPeriod saved = store.savePeriod(period);
        auditWriter.append(audit(tenantId, by, "payroll.period_closed", "pay_period", id.value(), null, null));
        eventPublisher.publishEvent(new PayPeriodClosed(tenantId, id));
        return saved;
    }

    @Transactional
    public PayPeriod reopen(TenantId tenantId, PayPeriodId id, UserId by, String reason) {
        PayPeriod period = get(tenantId, id);
        period.reopen(by, Instant.now());
        PayPeriod saved = store.savePeriod(period);
        auditWriter.append(audit(tenantId, by, "payroll.period_reopened", "pay_period", id.value(), null, reason));
        return saved;
    }

    @Transactional
    public PayrollExport export(TenantId tenantId, PayPeriodId id, UserId by) {
        PayPeriod period = get(tenantId, id);
        if (period.state() != PayPeriodState.CLOSED) {
            throw new ConflictException("payroll.not_closed", "Period must be closed before export");
        }
        List<PayrollAttendanceLine> lines = dataSource.findAttendance(tenantId, period.startDate(), period.endDate());
        PayrollProjection projection = toProjection(id, lines);
        byte[] bytes = exporter.export(projection);
        String checksum = sha256(bytes);
        int version = store.findLatestExport(tenantId, id).map(e -> e.version() + 1).orElse(1);
        PayrollExport export = new PayrollExport(PayrollExportId.newId(), tenantId, id, version, checksum,
                exporter.format(), by, Instant.now());
        PayrollExport saved = store.saveExport(export);
        auditWriter.append(audit(tenantId, by, "payroll.export_generated", "payroll_export", saved.id().value(), null, null));
        eventPublisher.publishEvent(new PayrollExportGenerated(tenantId, saved.id()));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<PayrollExport> exports(TenantId tenantId, PayPeriodId id) {
        return store.findExports(tenantId, id);
    }

    private PayrollReadiness summarize(List<PayrollAttendanceLine> lines) {
        Minutes regular = Minutes.ZERO;
        Minutes overtime = Minutes.ZERO;
        int unresolved = 0;
        for (PayrollAttendanceLine line : lines) {
            regular = regular.plus(line.regularMinutes());
            overtime = overtime.plus(line.overtimeMinutes());
            if (line.hasOpenException()) {
                unresolved++;
            }
        }
        return new PayrollReadiness(lines, lines.size(), unresolved, regular, overtime);
    }

    private PayrollProjection toProjection(PayPeriodId id, List<PayrollAttendanceLine> lines) {
        List<PayrollProjection.Line> projected = lines.stream()
                .map(l -> new PayrollProjection.Line(l.employeeId(), l.regularMinutes(), l.overtimeMinutes(), Minutes.ZERO))
                .toList();
        return new PayrollProjection(id, projected);
    }

    private AuditEvent audit(TenantId tenantId, UserId actorId, String action, String entityType, UUID entityId,
                             String before, String after) {
        return new AuditEvent(AuditEventId.newId(), tenantId, actorId, action, entityType, entityId, before, after,
                UUID.randomUUID().toString(), Instant.now());
    }

    private static String sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
