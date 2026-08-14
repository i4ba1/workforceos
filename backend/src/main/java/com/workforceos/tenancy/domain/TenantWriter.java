package com.workforceos.tenancy.domain;

/** Write-side port for tenant persistence. */
public interface TenantWriter {

    Tenant save(Tenant tenant);
}
