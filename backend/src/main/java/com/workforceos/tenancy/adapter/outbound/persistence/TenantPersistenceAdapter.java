package com.workforceos.tenancy.adapter.outbound.persistence;

import com.workforceos.shared.id.TenantId;
import com.workforceos.tenancy.domain.Tenant;
import com.workforceos.tenancy.domain.TenantReader;
import com.workforceos.tenancy.domain.TenantStatus;
import com.workforceos.tenancy.domain.TenantWriter;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;

/** Maps between the {@link Tenant} aggregate and its JPA representation. */
@Repository
public class TenantPersistenceAdapter implements TenantReader, TenantWriter {

    private final TenantJpaRepository repository;

    public TenantPersistenceAdapter(TenantJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Tenant> findById(TenantId id) {
        return repository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Tenant> findByCode(String code) {
        return repository.findByCode(code).map(this::toDomain);
    }

    @Override
    public Tenant save(Tenant tenant) {
        TenantJpaEntity entity = new TenantJpaEntity(
                tenant.id().value(),
                tenant.code(),
                tenant.name(),
                tenant.defaultZone().getId(),
                tenant.locale().toLanguageTag(),
                tenant.status().name(),
                tenant.retentionDays());
        return toDomain(repository.save(entity));
    }

    private Tenant toDomain(TenantJpaEntity entity) {
        return new Tenant(
                new TenantId(entity.getId()),
                entity.getCode(),
                entity.getName(),
                ZoneId.of(entity.getDefaultZone()),
                Locale.forLanguageTag(entity.getLocale()),
                TenantStatus.valueOf(entity.getStatus()),
                entity.getRetentionDays());
    }
}
