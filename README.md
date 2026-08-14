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

| Phase | Scope |
|-------|-------|
| 0 | Foundation (this scaffold): modules, domain model, build, CI |
| 1 | People & Schedule |
| 2 | Time Capture (idempotent raw events) |
| 3 | Attendance Engine (calculation, policy strategies, DST/cross-midnight) |
| 4 | Approval (corrections, manager queue, optimistic locking, audit) |
| 5 | Payroll (period close/reopen, deterministic CSV export) |
| 6 | Production Quality (observability, load tests, security gates, synthetic scale) |
| 7 | Optional advanced (Kafka/outbox, external HRIS/terminal adapters, read replica) |
