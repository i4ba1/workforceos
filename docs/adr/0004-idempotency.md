# ADR 0004 — Idempotency

## Status
Accepted

## Context
Clock events arrive from devices/terminals that retry on network failure. Retries must
never create duplicate logical punches, and payroll exports must be reproducible.

## Decision
- User/device operations that may be retried require an `Idempotency-Key` (or source
  event identity). The same key/payload returns the original logical result.
- Inbound integrations require an external source + stable external event ID for
  deduplication, backed by a database unique constraint.
- Same idempotency key with a different payload is a conflict.

## Consequences
- Duplicate submission produces no duplicate raw event.
- Raw time events remain append-only and immutable.
