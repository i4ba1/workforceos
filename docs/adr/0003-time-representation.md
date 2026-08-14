# ADR 0003 — Time Representation

## Status
Accepted

## Context
Multi-country operation requires correct handling of DST transitions, cross-midnight
shifts and payroll semantics. Deriving payable duration from local date-time values
across time-zone transitions is incorrect.

## Decision
- Persist event instants in UTC using a type semantically equivalent to `java.time.Instant`.
- Persist IANA zone identifiers (`Asia/Jakarta`, `Europe/Berlin`) as the source of truth,
  not numeric offsets.
- Persist business date separately where reporting/payroll semantics require it; business
  date is anchored to scheduled shift start (configurable per policy).
- API responses include ISO-8601 instants and explicit business-time-zone metadata.

## Consequences
- DST spring-forward and fall-back cases are unambiguous and deterministically testable.
- Business-date resolution is a first-class, testable strategy.
