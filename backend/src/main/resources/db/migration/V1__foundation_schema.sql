CREATE TABLE tenant (
    id             uuid PRIMARY KEY,
    code           varchar(64)  NOT NULL,
    name           varchar(255) NOT NULL,
    default_zone   varchar(64)  NOT NULL,
    locale         varchar(16)  NOT NULL,
    status         varchar(32)  NOT NULL,
    retention_days integer      NOT NULL DEFAULT 365,
    CONSTRAINT uq_tenant_code UNIQUE (code)
);

CREATE TABLE legal_entity (
    id           uuid PRIMARY KEY,
    tenant_id    uuid         NOT NULL REFERENCES tenant (id),
    name         varchar(255) NOT NULL,
    primary_zone varchar(64)  NOT NULL
);
CREATE INDEX idx_legal_entity_tenant ON legal_entity (tenant_id);

CREATE TABLE org_unit (
    id        uuid PRIMARY KEY,
    tenant_id uuid         NOT NULL REFERENCES tenant (id),
    name      varchar(255) NOT NULL,
    parent_id uuid REFERENCES org_unit (id)
);
CREATE INDEX idx_org_unit_tenant ON org_unit (tenant_id);

CREATE TABLE work_location (
    id        uuid PRIMARY KEY,
    tenant_id uuid         NOT NULL REFERENCES tenant (id),
    name      varchar(255) NOT NULL,
    zone_id   varchar(64)  NOT NULL
);
CREATE INDEX idx_work_location_tenant ON work_location (tenant_id);

CREATE TABLE employee (
    id            uuid PRIMARY KEY,
    tenant_id     uuid         NOT NULL REFERENCES tenant (id),
    employee_no   varchar(64)  NOT NULL,
    first_name    varchar(128) NOT NULL,
    last_name     varchar(128) NOT NULL,
    email         varchar(255),
    status        varchar(32)  NOT NULL,
    linked_user_id uuid,
    CONSTRAINT uq_employee_tenant_no UNIQUE (tenant_id, employee_no)
);
CREATE INDEX idx_employee_tenant ON employee (tenant_id);

CREATE TABLE employment_assignment (
    id             uuid PRIMARY KEY,
    tenant_id      uuid NOT NULL REFERENCES tenant (id),
    employee_id    uuid NOT NULL REFERENCES employee (id),
    org_unit_id    uuid NOT NULL REFERENCES org_unit (id),
    manager_id     uuid REFERENCES employee (id),
    policy_id      uuid,
    effective_from date NOT NULL,
    effective_to   date
);
CREATE INDEX idx_employment_assignment_emp ON employment_assignment (tenant_id, employee_id, effective_from);

CREATE TABLE shift_template (
    id            uuid PRIMARY KEY,
    tenant_id     uuid         NOT NULL REFERENCES tenant (id),
    name          varchar(255) NOT NULL,
    local_start   time         NOT NULL,
    local_end     time         NOT NULL,
    zone_id       varchar(64)  NOT NULL,
    break_minutes bigint       NOT NULL DEFAULT 0,
    break_paid    boolean      NOT NULL DEFAULT false
);
CREATE INDEX idx_shift_template_tenant ON shift_template (tenant_id);

CREATE TABLE schedule_entry (
    id            uuid PRIMARY KEY,
    tenant_id     uuid        NOT NULL REFERENCES tenant (id),
    employee_id   uuid        NOT NULL REFERENCES employee (id),
    business_date date        NOT NULL,
    zone_id       varchar(64) NOT NULL,
    planned_start timestamptz NOT NULL,
    planned_end   timestamptz NOT NULL,
    version       bigint      NOT NULL DEFAULT 0
);
CREATE INDEX idx_schedule_entry_emp_date ON schedule_entry (tenant_id, employee_id, business_date);
