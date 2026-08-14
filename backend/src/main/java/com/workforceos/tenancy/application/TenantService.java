package com.workforceos.tenancy.application;

import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.error.NotFoundException;
import com.workforceos.shared.id.TenantId;
import com.workforceos.tenancy.domain.Tenant;
import com.workforceos.tenancy.domain.TenantReader;
import com.workforceos.tenancy.domain.TenantWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Locale;

/** Use-cases for tenant lifecycle and configuration. */
@Service
public class TenantService {

    private final TenantReader reader;
    private final TenantWriter writer;

    public TenantService(TenantReader reader, TenantWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Transactional
    public Tenant create(String code, String name, ZoneId defaultZone, Locale locale) {
        reader.findByCode(code).ifPresent(ignored -> {
            throw new ConflictException("tenant.code_taken", "Tenant code already exists: " + code);
        });
        return writer.save(new Tenant(TenantId.newId(), code, name, defaultZone, locale));
    }

    @Transactional(readOnly = true)
    public Tenant get(TenantId id) {
        return reader.findById(id)
                .orElseThrow(() -> new NotFoundException("tenant.not_found", "Tenant not found: " + id));
    }
}
