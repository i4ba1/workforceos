package com.workforceos.scheduling.application;

import com.workforceos.scheduling.domain.BreakConfig;
import com.workforceos.scheduling.domain.ScheduleEntry;
import com.workforceos.scheduling.domain.ScheduleReader;
import com.workforceos.scheduling.domain.ScheduleWriter;
import com.workforceos.scheduling.domain.ShiftTemplate;
import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.id.ShiftTemplateId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;
import com.workforceos.shared.time.Minutes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

/** Use-cases for shift templates and employee schedules. */
@Service
public class SchedulingService {

    private final ScheduleReader reader;
    private final ScheduleWriter writer;

    public SchedulingService(ScheduleReader reader, ScheduleWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Transactional
    public ShiftTemplate createShiftTemplate(TenantId tenantId, String name, LocalTime localStart, LocalTime localEnd,
                                             ZoneId zoneId, long breakMinutes, boolean breakPaid) {
        BreakConfig breakConfig = new BreakConfig(Minutes.of(breakMinutes), breakPaid);
        return writer.saveShiftTemplate(
                new ShiftTemplate(ShiftTemplateId.newId(), tenantId, name, localStart, localEnd, zoneId, breakConfig));
    }

    @Transactional(readOnly = true)
    public List<ShiftTemplate> listShiftTemplates(TenantId tenantId) {
        return reader.findShiftTemplates(tenantId);
    }

    @Transactional
    public ScheduleEntry createScheduleEntry(TenantId tenantId, EmployeeId employeeId, BusinessDate businessDate,
                                             ZoneId zoneId, Instant plannedStart, Instant plannedEnd) {
        List<ScheduleEntry> overlaps = reader.findOverlapping(tenantId, employeeId, plannedStart, plannedEnd);
        if (!overlaps.isEmpty()) {
            throw new ConflictException("schedule.overlap", "Schedule entry overlaps an existing entry for the employee");
        }
        return writer.saveScheduleEntry(
                new ScheduleEntry(ScheduleEntryId.newId(), tenantId, employeeId, businessDate, zoneId,
                        plannedStart, plannedEnd, 0L));
    }

    @Transactional(readOnly = true)
    public List<ScheduleEntry> listEntries(TenantId tenantId, EmployeeId employeeId, BusinessDate from, BusinessDate to) {
        return reader.findEntries(tenantId, employeeId, from, to);
    }
}
