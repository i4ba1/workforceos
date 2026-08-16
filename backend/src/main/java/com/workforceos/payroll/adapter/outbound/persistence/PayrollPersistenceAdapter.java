package com.workforceos.payroll.adapter.outbound.persistence;

import com.workforceos.payroll.domain.PayPeriod;
import com.workforceos.payroll.domain.PayPeriodState;
import com.workforceos.payroll.domain.PayrollExport;
import com.workforceos.payroll.domain.PayrollStore;
import com.workforceos.shared.id.PayPeriodId;
import com.workforceos.shared.id.PayrollExportId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Maps between payroll aggregates and their JPA representations. */
@Repository
public class PayrollPersistenceAdapter implements PayrollStore {

    private final PayPeriodJpaRepository periodRepository;
    private final PayrollExportJpaRepository exportRepository;

    public PayrollPersistenceAdapter(PayPeriodJpaRepository periodRepository,
                                     PayrollExportJpaRepository exportRepository) {
        this.periodRepository = periodRepository;
        this.exportRepository = exportRepository;
    }

    @Override
    public Optional<PayPeriod> findPeriod(TenantId tenantId, PayPeriodId id) {
        return periodRepository.findByTenantIdAndId(tenantId.value(), id.value()).map(this::toDomain);
    }

    @Override
    public List<PayPeriod> findPeriods(TenantId tenantId) {
        return periodRepository.findAllByTenantIdOrderByStartDateAsc(tenantId.value()).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public PayPeriod savePeriod(PayPeriod period) {
        PayPeriodJpaEntity entity = new PayPeriodJpaEntity(
                period.id().value(),
                period.tenantId().value(),
                period.startDate(),
                period.endDate(),
                period.state().name(),
                period.version(),
                period.closedBy() == null ? null : period.closedBy().value(),
                period.closedAt());
        return toDomain(periodRepository.save(entity));
    }

    @Override
    public Optional<PayrollExport> findLatestExport(TenantId tenantId, PayPeriodId periodId) {
        return exportRepository.findFirstByTenantIdAndPeriodIdOrderByVersionDesc(tenantId.value(), periodId.value())
                .map(this::toDomain);
    }

    @Override
    public List<PayrollExport> findExports(TenantId tenantId, PayPeriodId periodId) {
        return exportRepository.findAllByTenantIdAndPeriodIdOrderByVersionAsc(tenantId.value(), periodId.value())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public PayrollExport saveExport(PayrollExport export) {
        PayrollExportJpaEntity entity = new PayrollExportJpaEntity(
                export.id().value(),
                export.tenantId().value(),
                export.periodId().value(),
                export.version(),
                export.checksum(),
                export.format(),
                export.generatedBy().value(),
                export.generatedAt());
        return toDomain(exportRepository.save(entity));
    }

    private PayPeriod toDomain(PayPeriodJpaEntity entity) {
        return new PayPeriod(
                new PayPeriodId(entity.getId()),
                new TenantId(entity.getTenantId()),
                entity.getStartDate(),
                entity.getEndDate(),
                PayPeriodState.valueOf(entity.getState()),
                entity.getVersion(),
                entity.getClosedBy() == null ? null : new UserId(entity.getClosedBy()),
                entity.getClosedAt());
    }

    private PayrollExport toDomain(PayrollExportJpaEntity entity) {
        return new PayrollExport(
                new PayrollExportId(entity.getId()),
                new TenantId(entity.getTenantId()),
                new PayPeriodId(entity.getPeriodId()),
                entity.getVersion(),
                entity.getChecksum(),
                entity.getFormat(),
                new UserId(entity.getGeneratedBy()),
                entity.getGeneratedAt());
    }
}
