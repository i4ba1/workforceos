# WorkforceOS

Enterprise Time, Attendance & Workforce Management Platform.

A multi-tenant, time-zone-aware workforce management platform focused on the hardest
problems in time & attendance: scheduling, raw clock-event ingestion, attendance
calculation, configurable policies, exceptions, approvals, payroll readiness,
auditability and multinational operation.

This is a portfolio-grade **modular monolith** (Java 25 / Spring Boot 4.1 / Spring Modulith
+ React 19 / TypeScript 6 / Vite 8 / Material UI 9).

---

## Table of contents

1. [Architecture](#architecture)
2. [Tech stack](#tech-stack)
3. [Repository layout](#repository-layout)
4. [Prerequisites](#prerequisites)
5. [Local setup (step by step)](#local-setup-step-by-step)
6. [Configuration](#configuration)
7. [Seed / master data (runs once)](#seed--master-data-runs-once)
8. [Running](#running)
9. [Demo walkthrough](#demo-walkthrough)
10. [API surface](#api-surface)
11. [Build & test](#build--test)
12. [Observability & security](#observability--security)
13. [Documentation](#documentation)
14. [Roadmap](#roadmap)

---

## Architecture

A single deployable Spring Boot application with explicit, independently testable **domain
modules** (Spring Modulith). The deployment boundary is larger than the domain boundaries;
module dependencies are verified acyclic at build time.

```
com.workforceos.<module>/
├── domain/        aggregates, value objects, ports, domain services, events
├── application/   use-cases, transaction boundaries
├── adapter/       inbound (REST) and outbound (JPA/read-model) adapters
└── config/        Spring wiring
```

Modules: `tenancy`, `iam`, `people`, `organization`, `scheduling`, `timecapture`, `policy`,
`attendance`, `leave`, `approval`, `payroll`, `audit`, `notification`, `reporting`, plus a
`shared` kernel and a `web` infrastructure module.

Key engineering properties:

- **Time correctness** — events are `Instant` (UTC) + IANA `ZoneId` + business date;
  DST and cross-midnight shifts are calculated with instant arithmetic only.
- **Immutability** — raw time events are append-only; attendance records are derived and
  recalculable; published policy versions are effective-dated and immutable.
- **Idempotency** — clock events dedupe by idempotency key or source event id.
- **Concurrency** — approvals and payroll use optimistic locking (`@Version`).
- **Tenant isolation** — every tenant-owned query is scoped by `tenant_id` from the
  authenticated context; cross-tenant access returns "not found".

---

## Tech stack

| Layer        | Decision                                                        |
|--------------|-----------------------------------------------------------------|
| Runtime      | Java 25 LTS                                                     |
| Backend      | Spring Boot 4.1.x, Spring Modulith 2.1                          |
| Persistence  | PostgreSQL 17, Flyway (schema + seed)                           |
| Cache        | Redis (optional, provisioned but unused for the demo)           |
| Frontend     | React 19.2, TypeScript 6 (strict), Vite 8, Material UI 9        |
| Server state | TanStack Query 5, React Hook Form + Zod                         |
| Observability| Micrometer + Prometheus, OpenTelemetry tracing, structured logs |

---

## Repository layout

```
workforceos/
├── backend/       Spring Boot modular monolith (com.workforceos.*)
│   └── src/main/resources/db/migration/   Flyway schema + seed migrations
├── frontend/      React + TypeScript SPA (Vite)
├── docs/          ADRs, security, testing, performance
├── loadtest/      k6 load script
├── .github/       CI (build + security scans)
└── compose.yaml   Optional Postgres + Redis (not required if you use local Postgres)
```

---

## Prerequisites

- **JDK 25** (`JAVA_HOME` should point at it). Verify: `java -version`.
- **Node.js 22+** and npm.
- **PostgreSQL 17** running locally (see credentials below).
- Redis — *optional*; the demo runs without it.

---

## Local setup (step by step)

This guide assumes a local PostgreSQL at `localhost:5432` with user `postgres`, password
`root` (adjust via environment variables if yours differs — see
[Configuration](#configuration)).

### 1. Create the database (one time)

```sql
CREATE DATABASE workforceos;
```

Using `psql`:

```bash
psql -h localhost -p 5432 -U postgres -c "CREATE DATABASE workforceos;"
```

Or create it in pgAdmin. The tables and seed data are created automatically by Flyway on
first startup — you do not need to run any SQL by hand.

### 2. Run the backend

```bash
cd backend
# Windows
mvnw.cmd spring-boot:run
# macOS / Linux
./mvnw spring-boot:run
```

The first startup:

1. connects to `jdbc:postgresql://localhost:5432/workforceos`,
2. runs all Flyway migrations (schema + seed) exactly once,
3. starts on **http://localhost:8080**.

Watch for: `Started WorkforceOsApplication` in the console.

### 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173**. The Vite dev server proxies `/api` to
`http://localhost:8080`.

---

## Configuration

Backend settings live in `backend/src/main/resources/application.yml` and can be overridden
with environment variables:

| Property     | Default                                   | Env var       |
|--------------|-------------------------------------------|---------------|
| Database URL | `jdbc:postgresql://localhost:5432/workforceos` | `DB_URL`   |
| Username     | `postgres`                                | `DB_USERNAME` |
| Password     | `root`                                    | `DB_PASSWORD` |
| Server port  | `8080`                                    | `SERVER_PORT` |
| Profile      | `local`                                   | `SPRING_PROFILES_ACTIVE` |

Example with different credentials:

```bash
DB_USERNAME=other DB_PASSWORD=secret ./mvnw spring-boot:run
```

**Dev-mode identity:** the backend uses a local development identity (no login). Requests
carry an `X-Tenant-Id` header (default demo tenant `00000000-0000-0000-0000-000000000001`).
The frontend already sends it. Production replaces this with an OIDC/OAuth 2.0 resource
server (see `docs/adr/0006-authentication-authorization.md`).

---

## Seed / master data (runs once)

Reference/master data is delivered as **Flyway versioned migrations** in
`backend/src/main/resources/db/migration/`:

| Migration | Contents                                                        |
|-----------|-----------------------------------------------------------------|
| `V1`      | Schema: tenant, people, organization, scheduling                |
| `V2`      | **Seed** — demo tenant (`DEMO`, `Asia/Jakarta`)                 |
| `V3`      | time capture schema                                             |
| `V4`      | attendance schema                                               |
| `V5`      | approval + audit schema                                         |
| `V6`      | payroll schema                                                  |
| `V7`      | **Seed** — org unit, 2 employees, assignments, shift templates, schedules, time events |

How the "run once" behaviour works:

- Flyway records every applied migration in the `flyway_schema_history` table.
- On the first startup, Flyway applies `V1..V7`.
- On every subsequent startup, Flyway sees they are already applied and **skips them**
  (logs `Schema "public" is up to date. No migration necessary.`).
- As an extra safeguard, the seed inserts use `ON CONFLICT (id) DO NOTHING`, so they are
  idempotent even if re-run.

To reset the demo data: drop and recreate the `workforceos` database (or
`DROP SCHEMA public CASCADE; CREATE SCHEMA public;` inside it) and restart.

---

## Running

| Action                              | Command                                   |
|-------------------------------------|-------------------------------------------|
| Start backend                       | `cd backend && ./mvnw spring-boot:run`    |
| Start frontend (dev)                | `cd frontend && npm install && npm run dev` |
| Build frontend (production)         | `cd frontend && npm run build`            |
| Run backend tests                   | `cd backend && ./mvnw verify`             |

---

## Demo walkthrough

1. Open **Dashboard** — sees seeded attendance/exception/approval counts.
2. **People** — the two seeded employees (`EMP-001`, `EMP-002`).
3. **Schedule** — `Day 08-16` and `Night 22-06` shift templates and seeded entries.
4. **Time** — select an employee and clock in/out; each action uses an idempotency key.
5. **Attendance** — derived records and open exceptions; submit a correction.
6. **Approvals** — approve/reject the correction (optimistic version check).
7. **Payroll** — open a period, check readiness, close, export CSV (checksum + version).

The full flow: clock event → attendance calculation → exception → approval → payroll
close → export.

---

## API surface

Base path `/api/v1`. Tenant scope is derived from the `X-Tenant-Id` header (dev mode).

| Method | Endpoint                              | Purpose                                  |
|--------|---------------------------------------|------------------------------------------|
| GET/POST | `/tenants`                          | Tenant configuration                     |
| GET/POST | `/employees`, `/employees/{id}/assignments` | People & employment assignment   |
| POST/GET | `/org-units`, `/legal-entities`, `/work-locations` | Organization structure |
| POST/GET | `/shift-templates`, `/schedule-entries` | Scheduling (overlap validated)       |
| POST     | `/time-events`                       | Clock in/out (idempotent, `Idempotency-Key`) |
| GET      | `/employees/{id}/time-events`        | Raw event timeline                       |
| GET      | `/attendance`, `/attendance/{id}`    | Derived attendance + exceptions          |
| POST     | `/attendance/recalculate`            | Recalculate an employee/business date    |
| GET      | `/attendance/exceptions`             | Manager exception queue                  |
| POST     | `/attendance/{id}/corrections`       | Submit correction request                |
| GET/POST | `/approval-cases`                    | Manager approval queue                   |
| POST     | `/approval-cases/{id}/approve` / `reject` | Decision (optimistic `expectedVersion`) |
| GET/POST | `/pay-periods`                       | Pay periods                              |
| GET      | `/pay-periods/{id}/readiness`        | Readiness summary                        |
| POST     | `/pay-periods/{id}/close` / `reopen` | Close / reopen (audited)                 |
| POST/GET | `/pay-periods/{id}/exports`          | Deterministic CSV export + history       |
| GET      | `/audit-events`                      | Immutable audit stream                   |

Errors follow RFC 7807 Problem Details with a stable machine `code` and `correlationId`.

---

## Build & test

```bash
cd backend && ./mvnw verify   # compile + 44 tests + Modulith/ArchUnit + SpotBugs
cd frontend && npm run typecheck && npm test && npm run build
```

The test suite is the centrepiece of the domain engine — it covers cross-midnight shifts,
DST spring-forward/fall-back, idempotent replay/conflict, missing punches, late/early-leave,
absent, unscheduled work, overtime, break violations, optimistic-lock conflicts, and
deterministic payroll exports. See `docs/testing/test-catalogue.md`.

---

## Observability & security

- **Logs** — structured console logs with `correlationId`, `tenantId`, `traceId`; the
  `X-Correlation-Id` header is accepted/generated and echoed on every response and error.
- **Metrics** — `/actuator/prometheus`, health probes on `/actuator/health`.
- **Tracing** — OpenTelemetry (OTLP endpoint via `OTEL_EXPORTER_OTLP_ENDPOINT`).
- **Security gates (CI)** — SpotBugs in `mvnw verify`; gitleaks secret scan, OWASP
  dependency-check, and Trivy container scan in `.github/workflows/ci.yml`.
- Docs: `docs/security/threat-model.md`, `docs/security/security-checklist.md`.
- Load test: `loadtest/attendance-load.js` (see `docs/performance/load-test.md`).

---

## Documentation

| Document | Location |
|----------|----------|
| Product PRD | `WorkforceOS_Complete_Technical_PRD.pdf` |
| Architecture decisions | `docs/adr/` |
| Threat model | `docs/security/threat-model.md` |
| Security checklist | `docs/security/security-checklist.md` |
| Test catalogue | `docs/testing/test-catalogue.md` |
| Load & performance | `docs/performance/load-test.md` |
| Deployment | `docs/deployment.md` |

---

## Roadmap

| Phase | Scope | Status |
|-------|-------|--------|
| 0 | Foundation: modules, domain model, build, CI | done |
| 1 | People & Schedule | done |
| 2 | Time Capture (idempotent raw events) | done |
| 3 | Attendance Engine (calculation, policy strategies, DST/cross-midnight) | done |
| 4 | Approval (corrections, manager queue, optimistic locking, audit) | done |
| 5 | Payroll (period close/reopen, deterministic CSV export) | done |
| 6 | Production Quality (observability, security gates, seed data, UX) | done |
| 7 | Optional advanced (Kafka/outbox, external HRIS/terminal adapters, read replica) | optional |
