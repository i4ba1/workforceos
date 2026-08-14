package com.workforceos.timecapture.domain;

import com.workforceos.shared.id.TimeEventId;

/**
 * The result of a previously ingested idempotent request.
 *
 * @param timeEventId the original event created by the first submission
 * @param digest      hash of the original business payload, used to detect a conflicting
 *                    replay (same idempotency key, different payload)
 */
public record IngestionRecord(TimeEventId timeEventId, String digest) {
}
