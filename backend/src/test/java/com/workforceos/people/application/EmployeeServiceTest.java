package com.workforceos.people.application;

import com.workforceos.people.domain.Employee;
import com.workforceos.people.domain.EmployeeReader;
import com.workforceos.people.domain.EmployeeWriter;
import com.workforceos.people.domain.EmploymentAssignment;
import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.error.NotFoundException;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.TenantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployeeServiceTest {

    static class InMemoryPeople implements EmployeeReader, EmployeeWriter {
        private final List<Employee> employees = new ArrayList<>();
        private final List<EmploymentAssignment> assignments = new ArrayList<>();

        @Override
        public Optional<Employee> findById(TenantId tenantId, EmployeeId id) {
            return employees.stream().filter(e -> e.tenantId().equals(tenantId) && e.id().equals(id)).findFirst();
        }

        @Override
        public Optional<Employee> findByEmployeeNo(TenantId tenantId, String employeeNo) {
            return employees.stream()
                    .filter(e -> e.tenantId().equals(tenantId) && e.employeeNo().equals(employeeNo)).findFirst();
        }

        @Override
        public List<Employee> findAll(TenantId tenantId) {
            return employees.stream().filter(e -> e.tenantId().equals(tenantId)).toList();
        }

        @Override
        public List<EmploymentAssignment> findAssignments(TenantId tenantId, EmployeeId employeeId) {
            return assignments.stream()
                    .filter(a -> a.tenantId().equals(tenantId) && a.employeeId().equals(employeeId)).toList();
        }

        @Override
        public Employee save(Employee employee) {
            employees.add(employee);
            return employee;
        }

        @Override
        public void saveAssignment(EmploymentAssignment assignment) {
            assignments.add(assignment);
        }
    }

    private static final TenantId TENANT = TenantId.newId();

    private final InMemoryPeople store = new InMemoryPeople();
    private final EmployeeService service = new EmployeeService(store, store);

    private Employee existing;

    @BeforeEach
    void setUp() {
        existing = service.create(TENANT, "E-100", "Jane", "Doe", "jane@example.com");
    }

    @Test
    void create_assignsEmployeeNoAndEmail() {
        assertThat(existing.employeeNo()).isEqualTo("E-100");
        assertThat(existing.email()).isEqualTo("jane@example.com");
        assertThat(existing.id()).isNotNull();
    }

    @Test
    void create_duplicateEmployeeNo_throwsConflict() {
        assertThatThrownBy(() -> service.create(TENANT, "E-100", "John", "Smith", null))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void get_missing_throwsNotFound() {
        assertThatThrownBy(() -> service.get(TENANT, EmployeeId.newId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void assign_createsAssignmentWithinTenant() {
        OrgUnitId orgUnit = OrgUnitId.newId();
        var assignment = service.assign(TENANT, new AssignEmployeeCommand(
                existing.id(), orgUnit, null, null, LocalDate.of(2026, 8, 1), null));

        assertThat(assignment.employeeId()).isEqualTo(existing.id());
        assertThat(assignment.orgUnitId()).isEqualTo(orgUnit);
        assertThat(service.assignments(TENANT, existing.id())).containsExactly(assignment);
    }

    @Test
    void assign_unknownEmployee_throwsNotFound() {
        assertThatThrownBy(() -> service.assign(TENANT, new AssignEmployeeCommand(
                EmployeeId.newId(), OrgUnitId.newId(), null, null, LocalDate.of(2026, 8, 1), null)))
                .isInstanceOf(NotFoundException.class);
    }
}
