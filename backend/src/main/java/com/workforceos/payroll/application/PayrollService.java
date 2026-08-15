package com.workforceos.payroll.application;

import com.workforceos.audit.domain.AuditEvent;
import com.workforceos.audit.domain.AuditWriter;
import com.workforceos.payroll.domain.PayPeriod;
import com.workforceos.payroll.domain.PayPeriodState;
import com.workforceos.payroll.domain.PayrollDataSource;
import com.workforceos.payroll.domain.PayrollExport;
import com.workforceos.payroll.domain.PayrollExporter;
import com.workforceos.payroll.domain.PayrollProjection;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * Payroll period lifecycle: open, validate, close (blocked by unresolved exceptions),
 * reopen with reason, and deterministic, versioned, checksummed exports.
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

    @Transactional
    public PayPeriod validate(TenantId tenantId, PayPeriodId id) {
        PayPeriod period = get(tenantId, id);
        period.startValidation();
        return store.savePeriod(period);
    }

    @Transactional
    public PayPeriod close(TenantId tenantId, PayPeriodId id, UserId by, Instant at) {
        PayPeriod period = get(tenantId, id);
        if (period.state() == PayPeriodState.CLOSED) {
            throw new ConflictException("payroll.already_closed", "Pay period is already closed");
        }
        if (period.state() == PayPeriodState.OPEN || period.state() == PayPeriodState.REOPENED) {
            period.startValidation();
        }
        long open = dataSource.countOpenExceptions(tenantId, period.startDate(), period.endDate());
        if (open > 0) {
            throw new ConflictException("payroll.unresolved_exceptions",
                    open + " open exception(s) must be resolved before closing the period");
        }
        period.close(by, at);
        PayPeriod saved = store.savePeriod(period);
        auditWriter.append(audit(tenantId, by, "payroll.period_closed", "pay_period", id.value(), null, null));
        eventPublisher.publishEvent(new PayPeriodClosed(tenantId, saved.id()));
        return saved;
    }

    @Transactional
    public PayPeriod reopen(TenantId tenantId, PayPeriodId id, UserId by, Instant at, String reason) {
        PayPeriod period = get(tenantId, id);
        period.reopen(by, at);
        PayPeriod saved = store.savePeriod(period);
        auditWriter.append(audit(tenantId, by, "payroll.period_reopened", "pay_period", id.value(), null, reason));
        return saved;
    }

    @Transactional
    public PayrollExportResult export(TenantId tenantId, PayPeriodId id, UserId by) {
        PayPeriod period = get(tenantId, id);
        if (period.state() != PayPeriodState.CLOSED) {
            throw new ConflictException("payroll.not_closed", "Pay period must be closed before export");
        }
        List<PayrollProjection.Line> lines = dataSource.findTotals(tenantId, period.startDate(), period.endDate());
        byte[] content = exporter.export(new PayrollProjection(id, lines));

        Optional<PayrollExport> latest = store.findLatestExport(tenantId, id);
        if (latest.isPresent() && latest.get().version() == (int) period.version()) {
            return new PayrollExportResult(latest.get(), content);
        }

        PayrollExport payrollExport = new PayrollExport(
                PayrollExportId.newId(), tenantId, id, (int) period.version(), sha256(content),
                exporter.format(), by, Instant.now());
        PayrollExport saved = store.saveExport(payrollExport);
        auditWriter.append(audit(tenantId, by, "payroll.export_generated", "payroll_export", saved.id().value(), null, null));
        eventPublisher.publishEvent(new PayrollExportGenerated(tenantId, saved.id()));
        return new PayrollExportResult(saved, content);
    }

    @Transactional(readOnly = true)
    public List<PayrollExport> exports(TenantId tenantId, PayPeriodId id) {
        return store.findExports(tenantId, id);
    }

    private AuditEvent audit(TenantId tenantId, UserId actorId, String action, String entityType, UUID entityId,
                             String before, String after) {
        return new AuditEvent(AuditEventId.newId(), tenantId, actorId, action, entityType, entityId, before, after,
                UUID.randomUUID().toString(), Instant.now());
    }

    private static String sha256(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(content));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
