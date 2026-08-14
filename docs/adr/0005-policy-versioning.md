# ADR 0005 — Policy Versioning

## Status
Accepted

## Context
Attendance rules change over time; historical calculations must remain reproducible for
payroll and audit.

## Decision
Attendance policies are effective-dated and versioned. A published policy version is
immutable; future changes create a new effective-dated version. A historical calculation
remains attached to the published policy version in effect at that time unless explicit
reprocessing is authorized.

## Consequences
- Closed payroll periods and historical records are reproducible.
- Rule changes never silently mutate historical outcomes.
