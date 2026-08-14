# ADR 0001 — Modular Monolith over Microservices

## Status
Accepted

## Context
Time & attendance transactions are strongly relational and span scheduling,
time-capture, policy, attendance and payroll. A microservice-first approach would
introduce network failure modes and distributed-transaction complexity before any
independent scaling pressure exists.

## Decision
Deploy a single Spring Boot application (Spring Modulith) with explicit,
independently testable domain modules. The deployment boundary is intentionally
larger than the domain-module boundaries.

## Consequences
- Simple local development, integration tests, transactions and demo operations.
- Module boundaries, application events, ports and database-ownership conventions
  preserve an extraction path to services.
- Future service extraction requires documented evidence (scaling pressure, release
  ownership, fault isolation, regulatory segregation, availability).
