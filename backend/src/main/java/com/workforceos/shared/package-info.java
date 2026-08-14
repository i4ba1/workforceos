/**
 * Shared kernel for cross-module primitives only.
 *
 * <p>Contains immutable value objects (identifiers, time/payroll quantities) that are
 * referenced by multiple application modules. Business logic and aggregates live in
 * their owning module; this package must never become a dumping ground.</p>
 *
 * <p>Marked {@code OPEN} because every domain module may use these primitives.</p>
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Shared Kernel",
        type = org.springframework.modulith.ApplicationModule.Type.OPEN)
package com.workforceos.shared;
