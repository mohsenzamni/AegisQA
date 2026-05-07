package com.aegisqa.orchestration.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * Executes a callable with retry logic based on a RetryPolicy.
 */
@Slf4j
@Component
public class RetryExecutor {

    /**
     * Execute the callable, retrying on failure up to policy.maxAttempts times.
     *
     * @param callable the operation to execute
     * @param policy   retry configuration
     * @param label    human-readable label for logging
     * @param <T>      return type
     * @return the callable's result
     * @throws Exception if all attempts failed
     */
    public <T> T execute(Callable<T> callable, RetryPolicy policy, String label) throws Exception {
        Exception lastException = null;
        for (int attempt = 1; attempt <= policy.getMaxAttempts(); attempt++) {
            try {
                if (attempt > 1) {
                    long backoff = policy.backoffFor(attempt - 1);
                    log.info("Retrying '{}' attempt {}/{} after {}ms backoff", label, attempt, policy.getMaxAttempts(), backoff);
                    Thread.sleep(backoff);
                }
                return callable.call();
            } catch (Exception e) {
                lastException = e;
                if (isRetryable(e, policy)) {
                    log.warn("'{}' failed on attempt {}/{}: {}", label, attempt, policy.getMaxAttempts(), e.getMessage());
                } else {
                    log.error("'{}' failed with non-retryable error: {}", label, e.getMessage());
                    throw e;
                }
            }
        }
        log.error("'{}' exhausted all {} attempts", label, policy.getMaxAttempts());
        throw lastException;
    }

    private boolean isRetryable(Exception e, RetryPolicy policy) {
        return policy.getRetryOn().stream().anyMatch(retryType -> retryType.isInstance(e));
    }
}
