package com.workforceos.scheduling.adapter.outbound.persistence;

import com.workforceos.scheduling.domain.BreakConfig;
import com.workforceos.scheduling.domain.ScheduleEntry;
import com.workforceos.scheduling.domain.ScheduleReader;
import com.workforceos.scheduling.domain.ScheduleWriter;
import com.workforceos.scheduling.domain.ShiftTemplate;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.id.ShiftTemplateId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;
import com.workforceos.shared.time.Minutes;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/** Maps between scheduling aggregates and their JPA representations. */
@Repository
public class SchedulingPersistenceAdapter implements ScheduleReader, ScheduleWriter {

    private final ShiftTemplateJpaRepository shiftTemplateRepository;
    private final ScheduleEntryJpaRepository scheduleEntryRepository;

    public SchedulingPersistenceAdapter(ShiftTemplateJpaRepository shiftTemplateRepository,
                                        ScheduleEntryJpaRepository scheduleEntryRepository) {
        this.shiftTemplateRepository = shiftTemplateRepository;
        this.scheduleEntryRepository = scheduleEntryRepository;
    }

    @Override
    public Optional<ShiftTemplate> findShiftTemplate(TenantId tenantId, ShiftTemplateId id) {
        return shiftTemplateRepository.findByTenantIdAndId(tenantId.value(), id.value()).map(this::toDomain);
    }

    @Override
    public List<ShiftTemplate> findShiftTemplates(TenantId tenantId) {
        return shiftTemplateRepository.findAllByTenantId(tenantId.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ScheduleEntry> findEntries(TenantId tenantId, EmployeeId employeeId, BusinessDate from, BusinessDate to) {
        return scheduleEntryRepository
                .findAllByTenantIdAndEmployeeIdAndBusinessDateBetween(
                        tenantId.value(), employeeId.value(), from.value(), to.value())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<ScheduleEntry> findOverlapping(TenantId tenantId, EmployeeId employeeId, Instant start, Instant end) {
        return scheduleEntryRepository
                .findOverlapping(tenantId.value(), employeeId.value(), start, end)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public ShiftTemplate saveShiftTemplate(ShiftTemplate template) {
        ShiftTemplateJpaEntity entity = new ShiftTemplateJpaEntity(
                template.id().value(),
                template.tenantId().value(),
                template.name(),
                template.localStart(),
                template.localEnd(),
                template.zoneId().getId(),
                template.breakConfig().minimumBreakMinutes().value(),
                template.breakConfig().paid());
        return toDomain(shiftTemplateRepository.save(entity));
    }

    @Override
    public ScheduleEntry saveScheduleEntry(ScheduleEntry entry) {
        ScheduleEntryJpaEntity entity = new ScheduleEntryJpaEntity(
                entry.id().value(),
                entry.tenantId().value(),
                entry.employeeId().value(),
                entry.businessDate().value(),
                entry.zoneId().getId(),
                entry.plannedStart(),
                entry.plannedEnd(),
                entry.version());
        return toDomain(scheduleEntryRepository.save(entity));
    }

    private ShiftTemplate toDomain(ShiftTemplateJpaEntity entity) {
        return new ShiftTemplate(
                new ShiftTemplateId(entity.getId()),
                new TenantId(entity.getTenantId()),
                entity.getName(),
                entity.getLocalStart(),
                entity.getLocalEnd(),
                ZoneId.of(entity.getZoneId()),
                new BreakConfig(Minutes.of(entity.getBreakMinutes()), entity.isBreakPaid()));
    }

    private ScheduleEntry toDomain(ScheduleEntryJpaEntity entity) {
        return new ScheduleEntry(
                new ScheduleEntryId(entity.getId()),
                new TenantId(entity.getTenantId()),
                new EmployeeId(entity.getEmployeeId()),
                BusinessDate.of(entity.getBusinessDate()),
                ZoneId.of(entity.getZoneId()),
                entity.getPlannedStart(),
                entity.getPlannedEnd(),
                entity.getVersion());
    }
}
