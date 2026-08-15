package com.workforceos.attendance.adapter.outbound.persistence.readmodel;

import com.workforceos.attendance.domain.AttendanceDataSource;
import com.workforceos.attendance.domain.ClockEventKind;
import com.workforceos.attendance.domain.ClockEventReadModel;
import com.workforceos.attendance.domain.PlannedShift;
import com.workforceos.shared.error.NotFoundException;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Read-model adapter feeding the calculator from the schedule, time-event and tenant
 * tables. Reads are intentionally decoupled from the owning modules' write entities.
 */
@Repository
public class AttendanceDataSourceAdapter implements AttendanceDataSource {

    private final ScheduleEntryReadRepository scheduleRepository;
    private final TimeEventReadRepository timeEventRepository;
    private final TenantReadRepository tenantRepository;

    public AttendanceDataSourceAdapter(ScheduleEntryReadRepository scheduleRepository,
                                       TimeEventReadRepository timeEventRepository,
                                       TenantReadRepository tenantRepository) {
        this.scheduleRepository = scheduleRepository;
        this.timeEventRepository = timeEventRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public Optional<PlannedShift> findShift(TenantId tenantId, EmployeeId employeeId, BusinessDate businessDate) {
        return scheduleRepository
                .findFirstByTenantIdAndEmployeeIdAndBusinessDateOrderByPlannedStartAsc(
                        tenantId.value(), employeeId.value(), businessDate.value())
                .map(e -> new PlannedShift(
                        new ScheduleEntryId(e.getId()),
                        ZoneId.of(e.getZoneId()),
                        e.getPlannedStart(),
                        e.getPlannedEnd(),
                        BusinessDate.of(e.getBusinessDate())));
    }

    @Override
    public List<ClockEventReadModel> findEvents(TenantId tenantId, EmployeeId employeeId, Instant from, Instant to) {
        return timeEventRepository
                .findAllByTenantIdAndEmployeeIdAndOccurredAtBetweenOrderByOccurredAtAsc(
                        tenantId.value(), employeeId.value(), from, to)
                .stream()
                .map(e -> new ClockEventReadModel(e.getOccurredAt(), ClockEventKind.valueOf(e.getEventType()),
                        ZoneId.of(e.getZoneId())))
                .toList();
    }

    @Override
    public ZoneId tenantDefaultZone(TenantId tenantId) {
        return tenantRepository.findById(tenantId.value())
                .map(e -> ZoneId.of(e.getDefaultZone()))
                .orElseThrow(() -> new NotFoundException("tenant.not_found", "Tenant not found: " + tenantId));
    }
}
