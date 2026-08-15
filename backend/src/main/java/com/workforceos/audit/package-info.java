/**
 * Audit: immutable actor/action/entity audit stream.
 *
 * <p>Marked {@code OPEN} because multiple modules append and query audit evidence.</p>
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Audit",
        type = org.springframework.modulith.ApplicationModule.Type.OPEN)
package com.workforceos.audit;
