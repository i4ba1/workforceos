package com.workforceos.tenancy.domain;

import com.workforceos.shared.id.TenantId;

import java.util.Optional;

/** Read-side port for tenant lookups. */
public interface TenantReader {

    Optional<Tenant> findById(TenantId id);

    Optional<Tenant> findByCode(String code);
}
