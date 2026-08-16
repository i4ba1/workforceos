package com.workforceos.payroll.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface PayPeriodJpaRepository extends JpaRepository<PayPeriodJpaEntity, UUID> {

    Optional<PayPeriodJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    List<PayPeriodJpaEntity> findAllByTenantIdOrderByStartDateAsc(UUID tenantId);
}

interface PayrollExportJpaRepository extends JpaRepository<PayrollExportJpaEntity, UUID> {

    Optional<PayrollExportJpaEntity> findFirstByTenantIdAndPeriodIdOrderByVersionDesc(UUID tenantId, UUID periodId);

    List<PayrollExportJpaEntity> findAllByTenantIdAndPeriodIdOrderByVersionAsc(UUID tenantId, UUID periodId);
}
