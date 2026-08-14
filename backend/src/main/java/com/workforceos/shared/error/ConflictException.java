package com.workforceos.shared.error;

/**
 * Raised when a request violates a uniqueness or state invariant (e.g. duplicate employee
 * number, overlapping schedule, stale concurrent update).
 */
public class ConflictException extends RuntimeException {

    private final String code;

    public ConflictException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
