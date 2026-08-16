# Test Catalogue — domain edge cases

Maps the PRD mandatory edge-case catalogue (§15.2, DR-01..DR-12) to automated tests.

| # | Scenario | Rule | Test |
|---|----------|------|------|
| 1 | Cross-midnight shift | DR-01 | `AttendanceCalculatorTest.crossMidnightShift_anchorsToBusinessDateAndComputesFullDuration` |
| 2 | DST spring-forward (no phantom time) | DR-02 | `AttendanceCalculatorTest.dstSpringForward_doesNotCreatePhantomTime` |
| 3 | DST fall-back (repeated hour once) | DR-03 | `AttendanceCalculatorTest.dstFallBack_countsRepeatedHourExactlyOnce` |
| 4 | Duplicate idempotency key, same payload | DR-04 | `TimeCaptureServiceTest.record_sameKeySamePayload_returnsOriginalWithoutDuplicate` |
| 5 | Duplicate idempotency key, different payload → conflict | DR-04 | `TimeCaptureServiceTest.record_sameKeyDifferentPayload_throwsConflict` |
| 6 | Source-event dedup | DR-04 | `TimeCaptureServiceTest.record_sameSourceEventId_returnsOriginal` |
| 7 | Out-of-order event (timeline ordering) | DR-05 | `TimeCaptureServiceTest.timeline_returnsEventsInOccurrenceOrder` |
| 8 | Missing clock-out (no invented punch) | DR-06 | `AttendanceCalculatorTest.missingClockOut_firesMissingPunchWithoutInventingFinalPunch` |
| 9 | Late / early-leave | — | `AttendanceCalculatorTest.lateArrival_firesLate`, `earlyLeave_firesEarlyLeave` |
| 10 | Absent (no events) | — | `AttendanceCalculatorTest.noEvents_firesAbsent` |
| 11 | Unscheduled work | DR-12 | `AttendanceCalculatorTest.unscheduledWork_firesUnscheduledWork` |
| 12 | Overtime buckets | — | `AttendanceCalculatorTest.overtime_firesOvertimeAndSplitsBuckets` |
| 13 | Break violation | — | `AttendanceCalculatorTest.breakViolation_firesWhenRequiredBreakNotTaken` |
| 14 | Concurrent approval (stale version) | DR-09 | `ApprovalServiceTest.approve_withStaleVersion_throwsConflict` |
| 15 | Closed-period mutation blocked / reopen | DR-10 | `PayrollServiceTest.close_withUnresolvedRecords_throwsConflict`, `reopen_afterClose_reopensPeriod` |
| 16 | Reopen then new export version | DR-10 | `PayrollServiceTest.export_afterClose_isDeterministicAndVersioned` |
| 17 | Duplicate employee number | — | `EmployeeServiceTest.create_duplicateEmployeeNo_throwsConflict` |
| 18 | Schedule overlap | — | `SchedulingServiceTest.createScheduleEntry_overlapping_throwsConflict` |

## Architecture gates
- Module cycle / boundary verification: `ModularityTest.verifiesApplicationModules`.
- Domain isolation from adapters/frameworks: `ArchitectureTest.domain_is_isolated_from_adapters_and_frameworks`.
- Static analysis: SpotBugs (`threshold=High`) in `mvnw verify`.

## Persistence (PostgreSQL/Testcontainers)
- Run on a Docker-enabled host: unique idempotency, optimistic locking, and index/query behavior
  are exercised via Testcontainers (see `backend` test scope and PRD §15.1).
