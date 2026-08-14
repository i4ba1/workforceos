# ADR 0007 — Payroll Period Close

## Status
Accepted

## Context
Closed-period data must never be silently mutated. Payroll exports must be reproducible
and attributable.

## Decision
- Close validates readiness and blocks if required records are unresolved, unless an
  authorized override policy explicitly permits it.
- Close snapshots finalized attendance totals and locks normal edits for the period.
- Reopen/amendment requires elevated permission, mandatory reason, audit event, and a new
  export version after re-approval.
- Exports are deterministic with checksum/version and record the generating actor.

## Consequences
- Payroll data is reproducible and traceable end to end.
- Normal mutations to a closed period are blocked; a governed amendment flow is required.
