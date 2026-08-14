package com.workforceos.scheduling.domain;

import com.workforceos.shared.time.Minutes;

/**
 * Break configuration of a shift template.
 *
 * @param minimumBreakMinutes minimum required break duration
 * @param paid                whether the break counts as payable time
 */
public record BreakConfig(Minutes minimumBreakMinutes, boolean paid) {

    public BreakConfig {
        if (minimumBreakMinutes == null) {
            throw new IllegalArgumentException("minimumBreakMinutes must not be null");
        }
    }
}
