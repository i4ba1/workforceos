# Threat Model — WorkforceOS

Scope: the WorkforceOS modular monolith (backend API, persistence, frontend SPA) as run for the
portfolio demo. Synthetic data only.

## Assets
- Immutable time-event history and derived attendance (integrity is the core business value).
- Payroll exports and audit evidence.
- Employee PII (names, emails) and tenant configuration.

## Trust boundaries
- Internet / client → API (untrusted).
- API → PostgreSQL / Redis (trusted, internal).
- Module → module within the monolith (trusted but boundary-enforced).
- Tenant A vs Tenant B (must be fully isolated).

## Threat scenarios and mitigations

| # | Threat | Mitigation | Status |
|---|--------|-----------|--------|
| T1 | Cross-tenant data access (IDOR / guessed UUIDs) | Tenant id derived from authenticated context; every repo query scoped by `tenant_id`; cross-tenant returns not-found | Implemented (negative tests) |
| T2 | Duplicate clock punches from retries | Idempotency key + source event id; unique constraints; digest conflict detection | Implemented |
| T3 | Silent mutation of closed payroll period | Close locks edits; reopen requires reason + audit + new export version | Implemented |
| T4 | Stale concurrent approval overwrite | Optimistic version check + `@Version` backstop → 409 | Implemented |
| T5 | PII leakage in logs | Structured logs avoid sensitive payloads; tenant/correlation only | Implemented |
| T6 | Secret exposure in repo | Secret scanning in CI (gitleaks); secrets from env/secret manager | Implemented |
| T7 | Dependency/CVE risk | OWASP dependency-check + container scan (Trivy) in CI | Implemented |
| T8 | Unvalidated input / injection | Bean validation; parameterized persistence APIs | Implemented |
| T9 | Forged auth (dev header) | `X-Tenant-Id` is dev-mode only; production uses OIDC resource server | Documented limitation |
| T10 | Replay of webhook/export | Out-of-scope for MVP; noted for external integration phase | Deferred |

## Known accepted risks (portfolio)
- Dev-mode identity filter trusts `X-Tenant-Id`/`X-User-Id` headers. Not for production.
- Row-Level Security (RLS) is available as defense-in-depth but not enabled (documented in ADR-0002).
- No rate limiting wired yet (Redis is provisioned but unused).
