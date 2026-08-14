package com.workforceos.scheduling.application;

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
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SchedulingServiceTest {

    static class InMemorySchedules implements ScheduleReader, ScheduleWriter {
        private final List<ShiftTemplate> templates = new ArrayList<>();
        private final List<ScheduleEntry> entries = new ArrayList<>();

        @Override
        public Optional<ShiftTemplate> findShiftTemplate(TenantId tenantId, ShiftTemplateId id) {
            return templates.stream().filter(t -> t.id().equals(id)).findFirst();
        }

        @Override
        public List<ShiftTemplate> findShiftTemplates(TenantId tenantId) {
            return List.copyOf(templates);
        }

        @Override
        public List<ScheduleEntry> findEntries(TenantId tenantId, EmployeeId employeeId, BusinessDate from, BusinessDate to) {
            return entries.stream()
                    .filter(e -> e.employeeId().equals(employeeId))
                    .filter(e -> !e.businessDate().isBefore(from) && !e.businessDate().isAfter(to))
                    .toList();
        }

        @Override
        public List<ScheduleEntry> findOverlapping(TenantId tenantId, EmployeeId employeeId, Instant start, Instant end) {
            return entries.stream()
                    .filter(e -> e.employeeId().equals(employeeId))
                    .filter(e -> e.plannedStart().isBefore(end) && start.isBefore(e.plannedEnd()))
                    .toList();
        }

        @Override
        public ShiftTemplate saveShiftTemplate(ShiftTemplate template) {
            templates.add(template);
            return template;
        }

        @Override
        public ScheduleEntry saveScheduleEntry(ScheduleEntry entry) {
            entries.add(entry);
            return entry;
        }
    }

    private static final TenantId TENANT = TenantId.newId();
    private static final EmployeeId EMPLOYEE = EmployeeId.newId();
    private static final ZoneId JAKARTA = ZoneId.of("Asia/Jakarta");

    private final InMemorySchedules store = new InMemorySchedules();
    private final SchedulingService service = new SchedulingService(store, store);

    @Test
    void createShiftTemplate_persistsBreakConfig() {
        ShiftTemplate template = service.createShiftTemplate(TENANT, "Day", LocalTime.of(8, 0), LocalTime.of(17, 0),
                JAKARTA, 30, false);

        assertThat(template.breakConfig().minimumBreakMinutes().value()).isEqualTo(30);
        assertThat(template.breakConfig().paid()).isFalse();
        assertThat(store.findShiftTemplates(TENANT)).containsExactly(template);
    }

    @Test
    void createScheduleEntry_noOverlap_persists() {
        Instant start = Instant.parse("2026-08-14T01:00:00Z"); // 08:00 Jakarta
        Instant end = Instant.parse("2026-08-14T10:00:00Z");   // 17:00 Jakarta

        ScheduleEntry entry = service.createScheduleEntry(TENANT, EMPLOYEE, BusinessDate.of(2026, 8, 14), JAKARTA, start, end);

        assertThat(entry.id()).isNotNull();
        assertThat(store.findOverlapping(TENANT, EMPLOYEE, start, end)).containsExactly(entry);
    }

    @Test
    void createScheduleEntry_overlapping_throwsConflict() {
        Instant existingStart = Instant.parse("2026-08-14T01:00:00Z");
        Instant existingEnd = Instant.parse("2026-08-14T10:00:00Z");
        service.createScheduleEntry(TENANT, EMPLOYEE, BusinessDate.of(2026, 8, 14), JAKARTA, existingStart, existingEnd);

        Instant overlappingStart = Instant.parse("2026-08-14T05:00:00Z");
        Instant overlappingEnd = Instant.parse("2026-08-14T12:00:00Z");

        assertThatThrownBy(() -> service.createScheduleEntry(
                TENANT, EMPLOYEE, BusinessDate.of(2026, 8, 14), JAKARTA, overlappingStart, overlappingEnd))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("overlap");
    }

    @Test
    void createScheduleEntry_nonOverlappingEmployee_isAllowed() {
        Instant start = Instant.parse("2026-08-14T01:00:00Z");
        Instant end = Instant.parse("2026-08-14T10:00:00Z");
        service.createScheduleEntry(TENANT, EMPLOYEE, BusinessDate.of(2026, 8, 14), JAKARTA, start, end);

        ScheduleEntry other = service.createScheduleEntry(
                TENANT, EmployeeId.newId(), BusinessDate.of(2026, 8, 14), JAKARTA, start, end);

        assertThat(other.id()).isNotNull();
    }
}
