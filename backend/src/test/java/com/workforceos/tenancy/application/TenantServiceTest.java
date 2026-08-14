package com.workforceos.tenancy.application;

import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.id.TenantId;
import com.workforceos.tenancy.domain.Tenant;
import com.workforceos.tenancy.domain.TenantReader;
import com.workforceos.tenancy.domain.TenantWriter;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TenantServiceTest {

    static class InMemoryTenants implements TenantReader, TenantWriter {
        private final Map<TenantId, Tenant> tenants = new HashMap<>();

        @Override
        public Optional<Tenant> findById(TenantId id) {
            return Optional.ofNullable(tenants.get(id));
        }

        @Override
        public Optional<Tenant> findByCode(String code) {
            return tenants.values().stream().filter(t -> t.code().equals(code)).findFirst();
        }

        @Override
        public Tenant save(Tenant tenant) {
            tenants.put(tenant.id(), tenant);
            return tenant;
        }
    }

    private final InMemoryTenants store = new InMemoryTenants();
    private final TenantService service = new TenantService(store, store);

    @Test
    void create_persistsTenantWithGeneratedId() {
        Tenant tenant = service.create("DEMO", "Global Industrial Demo", ZoneId.of("Asia/Jakarta"), Locale.ENGLISH);

        assertThat(tenant.id()).isNotNull();
        assertThat(tenant.code()).isEqualTo("DEMO");
        assertThat(store.findById(tenant.id())).contains(tenant);
    }

    @Test
    void create_duplicateCode_throwsConflict() {
        service.create("DEMO", "First", ZoneId.of("Asia/Jakarta"), Locale.ENGLISH);

        assertThatThrownBy(() -> service.create("DEMO", "Second", ZoneId.of("Europe/Berlin"), Locale.GERMAN))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("DEMO");
    }

    @Test
    void get_returnsTenantById() {
        Tenant created = service.create("ACME", "Acme", ZoneId.of("Europe/London"), Locale.UK);

        assertThat(service.get(created.id())).isEqualTo(created);
    }

    @Test
    void get_unknownTenant_throwsNotFound() {
        assertThatThrownBy(() -> service.get(TenantId.newId()))
                .isInstanceOf(com.workforceos.shared.error.NotFoundException.class);
    }
}
