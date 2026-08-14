package com.workforceos.shared.time;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A business date as understood by payroll/reporting semantics.
 *
 * <p>For a cross-midnight shift the business date is anchored to the scheduled shift
 * start (per policy), not to the wall-clock date of every raw event. This value object
 * keeps that distinction explicit rather than relying on a bare {@link LocalDate}.</p>
 */
public record BusinessDate(LocalDate value) {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    public static BusinessDate of(LocalDate value) {
        return new BusinessDate(value);
    }

    public static BusinessDate of(int year, int month, int day) {
        return new BusinessDate(LocalDate.of(year, month, day));
    }

    public static BusinessDate parse(String value) {
        return new BusinessDate(LocalDate.parse(value, ISO));
    }

    public boolean isBefore(BusinessDate other) {
        return value.isBefore(other.value);
    }

    public boolean isAfter(BusinessDate other) {
        return value.isAfter(other.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
