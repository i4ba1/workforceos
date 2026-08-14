package com.workforceos.timecapture.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Client-submitted intent to record a time event, including idempotency metadata.
 *
 * @param idempotencyKey client-generated key making the submission idempotent
 * @param clientTime     client-reported timestamp
 * @param deviceId       optional originating device
 */
public record IngestionRequest(String idempotencyKey, Instant clientTime, String deviceId) {

    public IngestionRequest {
        Objects.requireNonNull(idempotencyKey, "idempotencyKey");
        Objects.requireNonNull(clientTime, "clientTime");
    }
}
