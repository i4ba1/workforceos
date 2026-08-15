CREATE TABLE approval_case (
    id           uuid PRIMARY KEY,
    tenant_id    uuid         NOT NULL REFERENCES tenant (id),
    subject_type varchar(64)  NOT NULL,
    subject_id   uuid         NOT NULL,
    opened_by    uuid         NOT NULL,
    opened_at    timestamptz  NOT NULL,
    state        varchar(32)  NOT NULL,
    version      bigint       NOT NULL DEFAULT 0,
    reason       varchar(512)
);
CREATE INDEX idx_approval_case_queue ON approval_case (tenant_id, state, opened_at);

CREATE TABLE approval_action (
    id           uuid PRIMARY KEY,
    tenant_id    uuid         NOT NULL REFERENCES tenant (id),
    case_id      uuid         NOT NULL REFERENCES approval_case (id),
    actor_id     uuid         NOT NULL,
    decision     varchar(32)  NOT NULL,
    reason       varchar(512) NOT NULL,
    acted_at     timestamptz  NOT NULL,
    case_version bigint       NOT NULL
);
CREATE INDEX idx_approval_action_case ON approval_action (tenant_id, case_id);

CREATE TABLE audit_event (
    id             uuid PRIMARY KEY,
    tenant_id      uuid         NOT NULL,
    actor_id       uuid         NOT NULL,
    action         varchar(128) NOT NULL,
    entity_type    varchar(64)  NOT NULL,
    entity_id      uuid         NOT NULL,
    before_digest  varchar(64),
    after_digest   varchar(64),
    correlation_id varchar(64)  NOT NULL,
    occurred_at    timestamptz  NOT NULL
);
CREATE INDEX idx_audit_event_entity ON audit_event (tenant_id, entity_type, entity_id, occurred_at);
CREATE INDEX idx_audit_event_actor ON audit_event (tenant_id, actor_id, occurred_at);
