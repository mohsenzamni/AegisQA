package com.aegisqa.orchestration.retry;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Configuration for retry behaviour on a step or execution.
 */
@Value
@Builder
public class RetryPolicy {

    @Builder.Default
    int maxAttempts = 3;

    @Builder.Default
    long initialBackoffMs = 500;

    @Builder.Default
    long maxBackoffMs = 10_000;

    @Builder.Default
    double backoffMultiplier = 2.0;

    @Builder.Default
    List<Class<? extends Throwable>> retryOn = List.of(RuntimeException.class);

    public static RetryPolicy defaultPolicy() {
        return RetryPolicy.builder().build();
    }

    public static RetryPolicy noRetry() {
        return RetryPolicy.builder().maxAttempts(1).build();
    }

    public long backoffFor(int attempt) {
        double backoff = initialBackoffMs * Math.pow(backoffMultiplier, attempt - 1);
        return Math.min((long) backoff, maxBackoffMs);
    }
}
