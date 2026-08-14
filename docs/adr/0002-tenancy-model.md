# ADR 0002 — Tenancy Model

## Status
Accepted

## Context
The platform serves multiple enterprise tenants that must be strongly isolated.

## Decision
Shared PostgreSQL database and schema with a mandatory `tenant_id` on every
tenant-owned table. Composite unique constraints and indexes include `tenant_id`.
Authorization context is derived from the authenticated claim, never from a
client-supplied tenant ID alone. Cache keys include the tenant identifier. Optional
PostgreSQL Row Level Security is reserved as defense-in-depth.

## Consequences
- Simple operations and single migration stream.
- A shared-schema bug is a severe data-exposure risk; mitigated by tenant context at
  auth/use-case/repository/cache layers and negative integration tests.
- Cross-tenant attempts return generic inaccessible/not-found semantics.
