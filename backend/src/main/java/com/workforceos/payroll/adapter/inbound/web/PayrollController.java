package com.workforceos.payroll.adapter.inbound.web;

import com.workforceos.payroll.adapter.inbound.web.PayrollDtos.ExportResponse;
import com.workforceos.payroll.adapter.inbound.web.PayrollDtos.OpenPayPeriodRequest;
import com.workforceos.payroll.adapter.inbound.web.PayrollDtos.PayPeriodResponse;
import com.workforceos.payroll.adapter.inbound.web.PayrollDtos.ReadinessResponse;
import com.workforceos.payroll.adapter.inbound.web.PayrollDtos.ReopenRequest;
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
    public PayPeriodResponse open(@Valid @RequestBody OpenPayPeriodRequest request) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return PayPeriodResponse.from(payrollService.open(tenantId, request.startDate(), request.endDate()));
    }

    @GetMapping("/{id}/readiness")
    public ReadinessResponse readiness(@PathVariable UUID id) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return ReadinessResponse.from(payrollService.readiness(tenantId, new PayPeriodId(id)));
    }

    @PostMapping("/{id}/close")
    public PayPeriodResponse close(@PathVariable UUID id) {
        var context = TenantContextHolder.require();
        return PayPeriodResponse.from(payrollService.close(context.tenantId(), new PayPeriodId(id), context.userId()));
    }

    @PostMapping("/{id}/reopen")
    public PayPeriodResponse reopen(@PathVariable UUID id, @Valid @RequestBody ReopenRequest request) {
        var context = TenantContextHolder.require();
        return PayPeriodResponse.from(
                payrollService.reopen(context.tenantId(), new PayPeriodId(id), context.userId(), request.reason()));
    }

    @PostMapping("/{id}/exports")
    public ExportResponse export(@PathVariable UUID id) {
        var context = TenantContextHolder.require();
        return ExportResponse.from(payrollService.export(context.tenantId(), new PayPeriodId(id), context.userId()));
    }

    @GetMapping("/{id}/exports")
    public List<ExportResponse> exports(@PathVariable UUID id) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return payrollService.exports(tenantId, new PayPeriodId(id)).stream().map(ExportResponse::from).toList();
    }
}
