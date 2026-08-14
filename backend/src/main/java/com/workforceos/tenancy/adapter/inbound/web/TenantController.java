package com.workforceos.tenancy.adapter.inbound.web;

import com.workforceos.shared.id.TenantId;
import com.workforceos.tenancy.adapter.inbound.web.TenantDtos.CreateTenantRequest;
import com.workforceos.tenancy.adapter.inbound.web.TenantDtos.TenantResponse;
import com.workforceos.tenancy.application.TenantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    public TenantResponse create(@Valid @RequestBody CreateTenantRequest request) {
        var tenant = tenantService.create(
                request.code(),
                request.name(),
                ZoneId.of(request.defaultZone()),
                Locale.forLanguageTag(request.locale()));
        return TenantResponse.from(tenant);
    }

    @GetMapping("/{id}")
    public TenantResponse get(@PathVariable UUID id) {
        return TenantResponse.from(tenantService.get(new TenantId(id)));
    }
}
