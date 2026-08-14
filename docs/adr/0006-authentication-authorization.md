# ADR 0006 — Authentication & Authorization

## Status
Accepted

## Context
Enterprise deployment requires SSO readiness and strict tenant isolation.

## Decision
- OIDC/OAuth 2.0 via Spring Security resource server; JWT or opaque bearer tokens.
  Audience, issuer, expiry and signing keys are validated; clock skew is bounded.
- Deny by default; authorization is applied at use-case boundary and query scope, not only
  at controllers.
- Role grants capability; organizational scope narrows visible business objects.
- Sensitive actions require explicit permissions and emit security-grade audit events.

## Consequences
- Tenant context is authoritative from the authenticated claim.
- A development profile provides local test identity for demo mode.
