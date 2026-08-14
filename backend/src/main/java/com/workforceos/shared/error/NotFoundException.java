package com.workforceos.shared.error;

/**
 * Raised when a referenced resource does not exist within the caller's tenant boundary.
 *
 * <p>Cross-tenant access is indistinguishable from "not found" to avoid leaking existence.</p>
 */
public class NotFoundException extends RuntimeException {

    private final String code;

    public NotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
