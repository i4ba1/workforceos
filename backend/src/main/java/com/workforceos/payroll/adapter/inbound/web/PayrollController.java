package com.workforceos.payroll.adapter.inbound.web;

import com.workforceos.payroll.adapter.inbound.web.PayrollDtos.OpenPeriodRequest;
import com.workforceos.payroll.adapter.inbound.web.PayrollDtos.PayPeriodResponse;
import com.workforceos.payroll.adapter.inbound.web.PayrollDtos.PayrollExportContentResponse;
import com.workforceos.payroll.adapter.inbound.web.PayrollDtos.PayrollExportResponse;
import com.workforceos.payroll.adapter.inbound.web.PayrollDtos.ReopenRequest;
import com.workforceos.payroll.application.PayrollExportResult;
import com.workforceos.payroll.application.PayrollService;
import com.workforceos.shared.context.TenantContextHolder;
import com.workforceos.shared.id.PayPeriodId;
import com.workforceos.shared.id.TenantId;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pay-periods")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping
    public List<PayPeriodResponse> list() {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return payrollService.list(tenantId).stream().map(PayPeriodResponse::from).toList();
    }

    @PostMapping
    public PayPeriodResponse open(@Valid @RequestBody OpenPeriodRequest request) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return PayPeriodResponse.from(payrollService.open(tenantId, request.startDate(), request.endDate()));
    }

    @GetMapping("/{id}")
    public PayPeriodResponse get(@PathVariable UUID id) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return PayPeriodResponse.from(payrollService.get(tenantId, new PayPeriodId(id)));
    }

    @PostMapping("/{id}/validate")
    public PayPeriodResponse validate(@PathVariable UUID id) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return PayPeriodResponse.from(payrollService.validate(tenantId, new PayPeriodId(id)));
    }

    @PostMapping("/{id}/close")
    public PayPeriodResponse close(@PathVariable UUID id) {
        var context = TenantContextHolder.require();
        return PayPeriodResponse.from(payrollService.close(context.tenantId(), new PayPeriodId(id),
                context.userId(), Instant.now()));
    }

    @PostMapping("/{id}/reopen")
    public PayPeriodResponse reopen(@PathVariable UUID id, @Valid @RequestBody ReopenRequest request) {
        var context = TenantContextHolder.require();
        return PayPeriodResponse.from(payrollService.reopen(context.tenantId(), new PayPeriodId(id),
                context.userId(), Instant.now(), request.reason()));
    }

    @PostMapping("/{id}/exports")
    public PayrollExportContentResponse export(@PathVariable UUID id) {
        var context = TenantContextHolder.require();
        PayrollExportResult result = payrollService.export(context.tenantId(), new PayPeriodId(id), context.userId());
        return new PayrollExportContentResponse(
                PayrollExportResponse.from(result.export()),
                new String(result.content(), StandardCharsets.UTF_8));
    }

    @GetMapping("/{id}/exports")
    public List<PayrollExportResponse> exports(@PathVariable UUID id) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return payrollService.exports(tenantId, new PayPeriodId(id)).stream()
                .map(PayrollExportResponse::from).toList();
    }
}
