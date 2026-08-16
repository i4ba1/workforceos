# Security Checklist (OWASP ASVS-aligned)

Reference: OWASP Application Security Verification Standard 5.x, applied to WorkforceOS.

## Authentication & Authorization
- [x] Deny by default; per-request authenticated subject + tenant context.
- [x] Authorization applied at use-case boundary (not only controllers) — tenant scoping on every repo query.
- [x] Sensitive actions (payroll close/reopen, export, approval, role change) emit audit events.
- [ ] Production OIDC/OAuth 2.0 resource server (dev-mode header identity is temporary).

## Tenant isolation
- [x] `tenant_id` on all tenant-owned tables; composite unique constraints include `tenant_id`.
- [x] Cache keys include tenant id (reserved).
- [x] Cross-tenant access returns generic not-found (no existence leak).
- [ ] PostgreSQL Row-Level Security (optional defense-in-depth, ADR-0002).

## Input & output
- [x] Bean Validation on inbound DTOs.
- [x] Parameterized persistence APIs (Spring Data JPA derived/JPQL queries).
- [x] RFC 7807 Problem Details with stable machine code + correlation id; no stack traces.

## Data protection
- [x] TLS in transit (deployment concern); encryption at rest (managed DB).
- [x] PII excluded from logs; correlation/tenant pseudonymous keys only.
- [x] Synthetic data only in repository.

## Software & dependency security
- [x] SpotBugs static analysis in `mvnw verify` (threshold High).
- [x] OWASP dependency-check in CI.
- [x] Container image scanning (Trivy) in CI.
- [x] Secret scanning (gitleaks) in CI.
- [ ] SAST depth (Semgrep) and SCA policy tuning can be layered on.

## Operational
- [x] Health probes; metrics (Prometheus); tracing (OTel).
- [x] Correlation id propagates across logs and error responses.
