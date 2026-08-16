package com.workforceos.payroll.adapter.outbound.persistence.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

interface PayrollAttendanceRecordReadRepository extends JpaRepository<PayrollAttendanceRecordReadEntity, UUID> {

    List<PayrollAttendanceRecordReadEntity> findAllByTenantIdAndBusinessDateBetween(
            UUID tenantId, LocalDate from, LocalDate to);
}

interface PayrollExceptionReadRepository extends JpaRepository<PayrollExceptionReadEntity, UUID> {

    List<PayrollExceptionReadEntity> findAllByTenantIdAndState(UUID tenantId, String state);
}
