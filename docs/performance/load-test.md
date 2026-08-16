# Load & performance testing

Reference NFRs (§14): p95 read < 400 ms, p95 command < 700 ms, clock ingestion 100 events/s,
single employee/day recalc < 200 ms.

## Tooling
- k6 for HTTP load (script: `loadtest/attendance-load.js`).
- Benchmarks to run against a seeded PostgreSQL (3 tenants / 10k employees / 90 days, §14.1).

## Run (on a Docker-enabled host)

```bash
# 1. Start app + Postgres (see README)
# 2. Warm up + smoke
k6 run --vus 5 --duration 30s loadtest/attendance-load.js
# 3. Sustained load
k6 run --vus 50 --duration 2m loadtest/attendance-load.js
```

## Key metrics to watch
- `/api/v1/attendance` (tenant daily list over 5k employees) — p95 latency.
- `/api/v1/time-events` (clock ingestion) — throughput + duplicate/conflict rate.
- `/api/v1/attendance/recalculate` — single employee/day recalculation latency.
- Database: connections, slow query count, lock wait.

## Notes
- The load script posts clock events with a small duplicate ratio to exercise idempotency.
- Reporting is via k6 output + the Prometheus/JVM metrics exposed on `/actuator`.
