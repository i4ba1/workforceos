package com.workforceos.approval.adapter.inbound.web;

import com.workforceos.approval.adapter.inbound.web.ApprovalDtos.ApprovalActionResponse;
import com.workforceos.approval.adapter.inbound.web.ApprovalDtos.ApprovalCaseDetailResponse;
import com.workforceos.approval.adapter.inbound.web.ApprovalDtos.ApprovalCaseResponse;
import com.workforceos.approval.adapter.inbound.web.ApprovalDtos.DecisionRequest;
import com.workforceos.approval.adapter.inbound.web.ApprovalDtos.OpenCaseRequest;
import com.workforceos.approval.application.ApprovalService;
import com.workforceos.shared.context.TenantContextHolder;
import com.workforceos.shared.id.ApprovalCaseId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
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
@RequestMapping("/api/v1/approval-cases")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping
    public List<ApprovalCaseResponse> queue() {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        return approvalService.queue(tenantId).stream().map(ApprovalCaseResponse::from).toList();
    }

    @PostMapping
    public ApprovalCaseResponse open(@Valid @RequestBody OpenCaseRequest request) {
        var context = TenantContextHolder.require();
        var approvalCase = approvalService.open(context.tenantId(), request.subjectType(), request.subjectId(),
                context.userId(), request.reason());
        return ApprovalCaseResponse.from(approvalCase);
    }

    @GetMapping("/{id}")
    public ApprovalCaseDetailResponse detail(@PathVariable UUID id) {
        TenantId tenantId = TenantContextHolder.require().tenantId();
        var approvalCase = approvalService.get(tenantId, new ApprovalCaseId(id));
        List<ApprovalActionResponse> actions = approvalService.actions(tenantId, new ApprovalCaseId(id)).stream()
                .map(ApprovalActionResponse::from).toList();
        return new ApprovalCaseDetailResponse(ApprovalCaseResponse.from(approvalCase), actions);
    }

    @PostMapping("/{id}/approve")
    public ApprovalCaseResponse approve(@PathVariable UUID id, @Valid @RequestBody DecisionRequest request) {
        var context = TenantContextHolder.require();
        var approvalCase = approvalService.approve(context.tenantId(), new ApprovalCaseId(id),
                context.userId(), request.expectedVersion(), request.reason());
        return ApprovalCaseResponse.from(approvalCase);
    }

    @PostMapping("/{id}/reject")
    public ApprovalCaseResponse reject(@PathVariable UUID id, @Valid @RequestBody DecisionRequest request) {
        var context = TenantContextHolder.require();
        var approvalCase = approvalService.reject(context.tenantId(), new ApprovalCaseId(id),
                context.userId(), request.expectedVersion(), request.reason());
        return ApprovalCaseResponse.from(approvalCase);
    }
}
