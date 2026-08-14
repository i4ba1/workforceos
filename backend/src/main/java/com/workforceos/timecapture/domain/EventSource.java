package com.workforceos.timecapture.domain;

import java.util.Objects;

/**
 * Identifies the originating system of a raw time event.
 *
 * <p>Together with {@code sourceEventId} this forms the external deduplication key for
 * inbound terminal/HRIS integrations.</p>
 *
 * @param source        source system identifier, e.g. {@code WEB_CLOCK}, {@code TERMINAL_7}
 * @param sourceEventId stable external event identifier
 */
public record EventSource(String source, String sourceEventId) {

    public EventSource {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(sourceEventId, "sourceEventId");
    }
}
