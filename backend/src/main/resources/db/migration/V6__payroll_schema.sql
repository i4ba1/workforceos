CREATE TABLE pay_period (
    id         uuid PRIMARY KEY,
    tenant_id  uuid        NOT NULL REFERENCES tenant (id),
    start_date date        NOT NULL,
    end_date   date        NOT NULL,
    state      varchar(32) NOT NULL,
    version    bigint      NOT NULL DEFAULT 0,
    closed_by  uuid,
    closed_at  timestamptz
);
CREATE INDEX idx_pay_period_tenant ON pay_period (tenant_id, start_date);

CREATE TABLE payroll_export (
    id           uuid PRIMARY KEY,
    tenant_id    uuid        NOT NULL REFERENCES tenant (id),
    period_id    uuid        NOT NULL REFERENCES pay_period (id),
    version      integer     NOT NULL,
    checksum     varchar(64) NOT NULL,
    format       varchar(16) NOT NULL,
    generated_by uuid        NOT NULL,
    generated_at timestamptz NOT NULL,
    CONSTRAINT uq_payroll_export_version UNIQUE (period_id, version)
);
CREATE INDEX idx_payroll_export_period ON payroll_export (tenant_id, period_id);
