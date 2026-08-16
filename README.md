# WorkforceOS

Enterprise Time, Attendance & Workforce Management Platform.

A multi-tenant, time-zone-aware workforce management platform focused on the hardest
problems in time & attendance: scheduling, raw clock-event ingestion, attendance
calculation, configurable policies, exceptions, approvals, payroll readiness,
auditability and multinational operation.

This repository is a portfolio-grade modular monolith. It is a **specification-first**
implementation scaffold: architecture, domain model, module boundaries, and build
tooling are provided; business use-cases are filled in phase by phase (see roadmap).

## Tech baseline

| Layer      | Decision                                            |
|------------|-----------------------------------------------------|
| Runtime    | Java 25 LTS                                          |
| Backend    | Spring Boot 4.1.x, Spring Modulith 2.1 (modular monolith) |
| Persistence| PostgreSQL 18, Flyway migrations                     |
| Cache      | Redis (rate limiting / short-lived cache)            |
| Frontend   | React 19.2, TypeScript 6 (strict), Vite 8, Material UI 9 |
| Server state | TanStack Query 5                                   |
| Build      | Maven wrapper (backend), npm/Vite (frontend)         |

## Repository layout

```
workforceos/
├── backend/     Spring Boot modular monolith (com.workforceos.*)
├── frontend/    React + TypeScript SPA
├── docs/adr/    Architectural Decision Records
└── compose.yaml Local Postgres + Redis
```

### Backend modules (Spring Modulith)

`tenancy`, `iam`, `people`, `organization`, `scheduling`, `timecapture`, `policy`,
`attendance`, `leave`, `approval`, `payroll`, `audit`, `notification`, `reporting`,
plus a `shared` kernel for cross-module primitives (identifiers, time value objects).

Each module follows a four-layer structure:

```
com.workforceos.<module>/
├── domain/        aggregates, value objects, ports, domain services, events
├── application/   use-cases, transaction boundaries, in/out ports
├── adapter/       inbound (REST) and outbound (JPA/clients) adapters
└── config/        Spring wiring
```

## Prerequisites

- JDK 25 (`JAVA_HOME` should point at it)
- Docker (for Postgres/Redis and Testcontainers tests)
- Node.js 22+ and npm

## Running locally

```bash
# 1. Start infrastructure
docker compose up -d

# 2. Backend (from repo root)
./backend/mvnw -f backend/pom.xml spring-boot:run
#   Windows: backend\mvnw.cmd -f backend\pom.xml spring-boot:run

# 3. Frontend
cd frontend && npm install && npm run dev
```

## Build & test

```bash
./backend/mvnw -f backend/pom.xml verify     # compile + unit + Modulith/ArchUnit + Testcontainers
cd frontend && npm run build                 # tsc strict + vite build
cd frontend && npm test                      # vitest
```

## Documentation

- [PRD](WorkforceOS_Complete_Technical_PRD.pdf)
- [Architecture Decision Records](docs/adr/)

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

## Observability & operations

- Structured logs with `correlationId` / `tenantId` / `traceId` (logback MDC via request filters).
- `X-Correlation-Id` accepted/generated and echoed on every response and Problem Details error body.
- Micrometer metrics on `/actuator/prometheus`; health probes `/actuator/health`.
- OpenTelemetry tracing (micrometer-tracing-bridge-otel) → OTLP endpoint (`OTEL_EXPORTER_OTLP_ENDPOINT`).

## Security

- SpotBugs (threshold High) runs in `mvnw verify`; OWASP dependency-check, gitleaks secret scan,
  and Trivy container scan run in CI (see `.github/workflows/ci.yml`).
- See `docs/security/threat-model.md` and `docs/security/security-checklist.md`.
- Flyway seeds a synthetic demo tenant + employees + schedules + events (`V2`, `V7`).

## Load testing

`k6` script and guidance in `docs/performance/load-test.md` (`loadtest/attendance-load.js`).
