package com.workforceos.shared.time;

import java.util.Objects;

/**
 * Whole-minute quantity used for worked, break, regular and overtime durations.
 *
 * <p>Payroll quantities are modeled as an explicit value object rather than a bare
 * {@code long} to avoid primitive obsession and to keep rounding/arithmetic semantics
 * in one place. Raw event instants are never derived from this value.</p>
 */
public record Minutes(long value) {

    public static final Minutes ZERO = new Minutes(0);

    public Minutes {
        if (value < 0) {
            throw new IllegalArgumentException("Minutes cannot be negative: " + value);
        }
    }

    public static Minutes of(long value) {
        return new Minutes(value);
    }

    public Minutes plus(Minutes other) {
        Objects.requireNonNull(other, "other");
        return new Minutes(value + other.value);
    }

    public Minutes minus(Minutes other) {
        Objects.requireNonNull(other, "other");
        long result = value - other.value;
        if (result < 0) {
            throw new IllegalArgumentException("Minutes would become negative");
        }
        return new Minutes(result);
    }

    public boolean isZero() {
        return value == 0;
    }

    public int compareTo(Minutes other) {
        return Long.compare(value, other.value);
    }
}
