package com.workforceos.shared.context;

/**
 * Thread-local holder for the current {@link TenantContext}.
 *
 * <p>Set by the web filter at the start of a request and cleared in a finally block so a
 * pooled thread never leaks a prior tenant's context.</p>
 */
public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void set(TenantContext context) {
        CONTEXT.set(context);
    }

    public static TenantContext get() {
        return CONTEXT.get();
    }

    public static TenantContext require() {
        TenantContext context = CONTEXT.get();
        if (context == null) {
            throw new IllegalStateException("No tenant context is bound to the current thread");
        }
        return context;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
