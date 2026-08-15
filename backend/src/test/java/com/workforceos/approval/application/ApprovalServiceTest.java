package com.workforceos.approval.application;

import com.workforceos.approval.domain.ApprovalAction;
import com.workforceos.approval.domain.ApprovalCase;
import com.workforceos.approval.domain.ApprovalCaseStore;
import com.workforceos.approval.domain.ApprovalState;
import com.workforceos.audit.domain.AuditEvent;
import com.workforceos.audit.domain.AuditWriter;
import com.workforceos.shared.error.ConflictException;
import com.workforceos.shared.id.ApprovalCaseId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApprovalServiceTest {

    static class FakeStore implements ApprovalCaseStore {
        final Map<ApprovalCaseId, ApprovalCase> cases = new LinkedHashMap<>();
        final List<ApprovalAction> actions = new ArrayList<>();

        @Override
        public Optional<ApprovalCase> findById(TenantId tenantId, ApprovalCaseId id) {
            return Optional.ofNullable(cases.get(id));
        }

        @Override
        public List<ApprovalCase> findOpen(TenantId tenantId) {
            return cases.values().stream().filter(c -> c.state() == ApprovalState.OPEN).toList();
        }

        @Override
        public ApprovalCase save(ApprovalCase approvalCase) {
            cases.put(approvalCase.id(), approvalCase);
            return approvalCase;
        }

        @Override
        public void saveAction(ApprovalAction action) {
            actions.add(action);
        }

        @Override
        public List<ApprovalAction> findActions(TenantId tenantId, ApprovalCaseId caseId) {
            return actions.stream().filter(a -> a.caseId().equals(caseId)).toList();
        }
    }

    private static final TenantId TENANT = TenantId.newId();
    private static final UserId EMPLOYEE = UserId.newId();
    private static final UserId MANAGER = UserId.newId();

    private final FakeStore store = new FakeStore();
    private final List<AuditEvent> audits = new ArrayList<>();
    private final List<Object> published = new ArrayList<>();
    private final ApprovalService service = new ApprovalService(store, new RecordingAuditWriter(audits), published::add);

    static class RecordingAuditWriter implements AuditWriter {
        private final List<AuditEvent> events;

        RecordingAuditWriter(List<AuditEvent> events) {
            this.events = events;
        }

        @Override
        public void append(AuditEvent event) {
            events.add(event);
        }
    }

    private ApprovalCase open() {
        return service.open(TENANT, "ATTENDANCE_RECORD", UUID.randomUUID(), EMPLOYEE, "Wrong total please fix");
    }

    @Test
    void open_createsOpenCaseWithVersionZero() {
        ApprovalCase approvalCase = open();

        assertThat(approvalCase.state()).isEqualTo(ApprovalState.OPEN);
        assertThat(approvalCase.version()).isZero();
        assertThat(audits).isNotEmpty();
    }

    @Test
    void approve_withCorrectVersion_approvesAndRecordsAction() {
        ApprovalCase approvalCase = open();

        ApprovalCase result = service.approve(TENANT, approvalCase.id(), MANAGER, 0L, "Approved");

        assertThat(result.state()).isEqualTo(ApprovalState.APPROVED);
        assertThat(result.version()).isEqualTo(1);
        assertThat(store.actions).hasSize(1);
        assertThat(store.actions.get(0).decision().name()).isEqualTo("APPROVE");
        assertThat(published).hasSize(1);
    }

    @Test
    void approve_withStaleVersion_throwsConflict() {
        ApprovalCase approvalCase = open();

        assertThatThrownBy(() -> service.approve(TENANT, approvalCase.id(), MANAGER, 7L, "Approved"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("version");
        assertThat(store.actions).isEmpty();
    }

    @Test
    void approve_alreadyDecidedCase_throwsConflict() {
        ApprovalCase approvalCase = open();
        service.approve(TENANT, approvalCase.id(), MANAGER, 0L, "Approved");

        assertThatThrownBy(() -> service.reject(TENANT, approvalCase.id(), MANAGER, 1L, "Reject"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already decided");
    }

    @Test
    void reject_marksRejected() {
        ApprovalCase approvalCase = open();

        ApprovalCase result = service.reject(TENANT, approvalCase.id(), MANAGER, 0L, "Rejected");

        assertThat(result.state()).isEqualTo(ApprovalState.REJECTED);
    }

    @Test
    void queue_returnsOnlyOpenCases() {
        ApprovalCase first = open();
        open();
        service.approve(TENANT, first.id(), MANAGER, 0L, "Approved");

        assertThat(service.queue(TENANT)).hasSize(1);
    }
}
