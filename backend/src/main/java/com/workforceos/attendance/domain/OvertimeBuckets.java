package com.workforceos.attendance.domain;

import com.workforceos.shared.time.Minutes;

/**
 * The result of splitting eligible worked minutes into regular and overtime buckets.
 */
public record OvertimeBuckets(Minutes regular, Minutes overtime) {

    public OvertimeBuckets {
        if (regular == null) {
            throw new IllegalArgumentException("regular must not be null");
        }
        if (overtime == null) {
            throw new IllegalArgumentException("overtime must not be null");
        }
    }
}
