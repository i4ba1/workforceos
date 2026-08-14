package com.workforceos.shared.result;

/**
 * Explicit outcome type for expected domain results.
 *
 * <p>Expected domain outcomes (e.g. a conflict, a validation failure, a duplicate
 * submission) are modeled as values so callers must handle them. Exceptions are
 * reserved for exceptional/technical conditions.</p>
 */
public sealed interface DomainResult<T> permits DomainResult.Success, DomainResult.Failure {

    record Success<T>(T value) implements DomainResult<T> {}

    record Failure<T>(String code, String message) implements DomainResult<T> {}

    default boolean isSuccess() {
        return this instanceof Success<T>;
    }

    default boolean isFailure() {
        return this instanceof Failure<T>;
    }
}
