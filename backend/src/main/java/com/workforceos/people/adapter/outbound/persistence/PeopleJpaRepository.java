package com.workforceos.people.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface EmployeeJpaRepository extends JpaRepository<EmployeeJpaEntity, UUID> {

    Optional<EmployeeJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

    Optional<EmployeeJpaEntity> findByTenantIdAndEmployeeNo(UUID tenantId, String employeeNo);

    List<EmployeeJpaEntity> findAllByTenantId(UUID tenantId);
}

interface EmploymentAssignmentJpaRepository extends JpaRepository<EmploymentAssignmentJpaEntity, UUID> {

    List<EmploymentAssignmentJpaEntity> findAllByTenantIdAndEmployeeId(UUID tenantId, UUID employeeId);
}
