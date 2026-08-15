package com.workforceos.attendance.domain;

import com.workforceos.shared.time.WorkInterval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard pairing: clock-in opens work, break-start/end split it, clock-out closes it.
 *
 * <p>All durations are derived from {@link Instant} arithmetic, so DST transitions never
 * create phantom worked time. An unclosed work interval is discarded and flagged as a
 * missing clock-out rather than inventing a final punch.</p>
 */
public class StandardEventPairingPolicy implements EventPairingPolicy {

    @Override
    public EventPairingResult pair(List<EventStamp> orderedStamps) {
        List<WorkInterval> worked = new ArrayList<>();
        List<WorkInterval> breaks = new ArrayList<>();
        Instant workStart = null;
        Instant breakStart = null;
        boolean clockInSeen = false;

        for (EventStamp stamp : orderedStamps) {
            switch (stamp.kind()) {
                case CLOCK_IN -> {
                    clockInSeen = true;
                    if (workStart == null) {
                        workStart = stamp.occurredAt();
                    }
                }
                case BREAK_START -> {
                    if (workStart != null) {
                        addIfValid(worked, workStart, stamp.occurredAt());
                        workStart = null;
                    }
                    breakStart = stamp.occurredAt();
                }
                case BREAK_END -> {
                    if (breakStart != null) {
                        addIfValid(breaks, breakStart, stamp.occurredAt());
                        breakStart = null;
                    }
                    if (workStart == null) {
                        workStart = stamp.occurredAt();
                    }
                }
                case CLOCK_OUT -> {
                    if (workStart != null) {
                        addIfValid(worked, workStart, stamp.occurredAt());
                        workStart = null;
                    }
                }
            }
        }

        boolean missingClockOut = workStart != null;
        return new EventPairingResult(List.copyOf(worked), List.copyOf(breaks), !clockInSeen, missingClockOut);
    }

    private void addIfValid(List<WorkInterval> target, Instant start, Instant end) {
        if (end.isAfter(start)) {
            target.add(WorkInterval.of(start, end));
        }
    }
}
