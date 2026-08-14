CREATE TABLE time_event (
    id              uuid PRIMARY KEY,
    tenant_id       uuid         NOT NULL REFERENCES tenant (id),
    employee_id     uuid         NOT NULL REFERENCES employee (id),
    event_type      varchar(32)  NOT NULL,
    occurred_at     timestamptz  NOT NULL,
    received_at     timestamptz  NOT NULL,
    zone_id         varchar(64)  NOT NULL,
    source          varchar(64)  NOT NULL,
    source_event_id varchar(128),
    CONSTRAINT uq_time_event_source UNIQUE (tenant_id, source, source_event_id)
);
CREATE INDEX idx_time_event_emp_occurred ON time_event (tenant_id, employee_id, occurred_at);

CREATE TABLE ingestion_request (
    id              uuid PRIMARY KEY,
    tenant_id       uuid         NOT NULL,
    idempotency_key varchar(128) NOT NULL,
    time_event_id   uuid         NOT NULL REFERENCES time_event (id),
    request_digest  varchar(64)  NOT NULL,
    created_at      timestamptz  NOT NULL,
    CONSTRAINT uq_ingestion_request_key UNIQUE (tenant_id, idempotency_key)
);
CREATE INDEX idx_ingestion_request_tenant ON ingestion_request (tenant_id);
