package com.workforceos.payroll.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface PayPeriodJpaRepository extends JpaRepository<PayPeriodJpaEntity, UUID> {

    Optional<PayPeriodJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    List<PayPeriodJpaEntity> findAllByTenantIdOrderByStartDateDesc(UUID tenantId);
}

interface PayrollExportJpaRepository extends JpaRepository<PayrollExportJpaEntity, UUID> {

    Optional<PayrollExportJpaEntity> findFirstByTenantIdAndPeriodIdOrderByVersionDesc(UUID tenantId, UUID periodId);

    List<PayrollExportJpaEntity> findAllByTenantIdAndPeriodId(UUID tenantId, UUID periodId);
}
