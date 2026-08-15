CREATE TABLE attendance_record (
    id                uuid PRIMARY KEY,
    tenant_id         uuid        NOT NULL REFERENCES tenant (id),
    employee_id       uuid        NOT NULL REFERENCES employee (id),
    business_date     date        NOT NULL,
    schedule_entry_id uuid REFERENCES schedule_entry (id),
    policy_version_id uuid,
    status            varchar(32) NOT NULL,
    regular_minutes   bigint      NOT NULL DEFAULT 0,
    overtime_minutes  bigint      NOT NULL DEFAULT 0,
    break_minutes     bigint      NOT NULL DEFAULT 0,
    version           bigint      NOT NULL DEFAULT 0,
    CONSTRAINT uq_attendance_record UNIQUE (tenant_id, employee_id, business_date)
);
CREATE INDEX idx_attendance_record_status ON attendance_record (tenant_id, status, business_date);

CREATE TABLE attendance_exception (
    id         uuid PRIMARY KEY,
    tenant_id  uuid         NOT NULL REFERENCES tenant (id),
    record_id  uuid         NOT NULL REFERENCES attendance_record (id),
    type       varchar(32)  NOT NULL,
    severity   varchar(16)  NOT NULL,
    state      varchar(16)  NOT NULL,
    detail     varchar(512) NOT NULL,
    created_at timestamptz  NOT NULL,
    CONSTRAINT uq_attendance_exception UNIQUE (record_id, type)
);
CREATE INDEX idx_attendance_exception_state ON attendance_exception (tenant_id, state, severity, created_at);
