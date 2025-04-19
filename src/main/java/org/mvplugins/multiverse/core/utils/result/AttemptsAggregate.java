package org.mvplugins.multiverse.core.utils.result;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Aggregates a list of {@link Attempt} objects into success and failure categories.
 * This class provides utility methods to handle successful and failed attempts separately.
 *
 * @param <T> The type of the successful result.
 * @param <F> The type representing failure reasons.
 */
public final class AttemptsAggregate<T, F extends FailureReason> {

    /**
     * Creates an {@link AttemptsAggregate} from a list of attempts.
     *
     * @param attempts The list of attempts to aggregate.
     * @param <T> The type of the successful result.
     * @param <F> The type representing failure reasons.
     * @return An instance of {@link AttemptsAggregate}.
     */
    public static <T, F extends FailureReason> AttemptsAggregate<T, F> allOf(List<Attempt<T, F>> attempts) {
        return new AttemptsAggregate<>(attempts);
    }

    /**
     * Creates an {@link AttemptsAggregate} from a varargs array of attempts.
     *
     * @param attempts The attempts to aggregate.
     * @param <T> The type of the successful result.
     * @param <F> The type representing failure reasons.
     * @return An instance of {@link AttemptsAggregate}.
     */
    public static <T, F extends FailureReason> AttemptsAggregate<T, F> allOf(Attempt<T, F>... attempts) {
        return new AttemptsAggregate<>(List.of(attempts));
    }

    /**
     * Creates an {@link AttemptsAggregate} with no attempts, representing an empty success state.
     * In this case, it will be deemed as successful, but attempt counts will be zero.
     *
     * @param <T> The type of the successful result.
     * @param <F> The type representing failure reasons.
     * @return An instance of {@link AttemptsAggregate} with no attempts.
     */
    public static <T, F extends FailureReason> AttemptsAggregate<T, F> emptySuccess() {
        return new AttemptsAggregate<>(Collections.emptyList());
    }

    private final List<Attempt<T, F>> successAttempts;
    private final List<Attempt<T, F>> failureAttempts;

    private AttemptsAggregate(List<Attempt<T, F>> attempts) {
        successAttempts = attempts.stream().filter(Attempt::isSuccess).toList();
        failureAttempts = attempts.stream().filter(Attempt::isFailure).toList();
    }

    /**
     * Executes the provided {@link Runnable} if there are successful attempts.
     *
     * @param runnable The action to execute on success.
     * @return This {@link AttemptsAggregate} instance for method chaining.
     */
    public AttemptsAggregate<T, F> onSuccess(Runnable runnable) {
        if (hasSuccess()) {
            runnable.run();
        }
        return this;
    }

    /**
     * Executes the provided {@link Runnable} if there are failed attempts.
     *
     * @param runnable The action to execute on failure.
     * @return This {@link AttemptsAggregate} instance for method chaining.
     */
    public AttemptsAggregate<T, F> onFailure(Runnable runnable) {
        if (hasFailure()) {
            runnable.run();
        }
        return this;
    }

    /**
     * Executes the provided {@link Consumer} with the list of successful attempts if any exist.
     *
     * @param successConsumer The consumer to process successful attempts.
     * @return This {@link AttemptsAggregate} instance for method chaining.
     */
    public AttemptsAggregate<T, F> onSuccess(Consumer<List<Attempt<T, F>>> successConsumer) {
        if (hasSuccess()) {
            successConsumer.accept(successAttempts);
        }
        return this;
    }

    /**
     * Executes the provided {@link Consumer} with the list of failed attempts if any exist.
     *
     * @param failureConsumer The consumer to process failed attempts.
     * @return This {@link AttemptsAggregate} instance for method chaining.
     */
    public AttemptsAggregate<T, F> onFailure(Consumer<List<Attempt<T, F>>> failureConsumer) {
        if (hasFailure()) {
            failureConsumer.accept(failureAttempts);
        }
        return this;
    }

    /**
     * Executes the provided {@link Consumer} with the count of successful attempts if any exist.
     * If count is 0 means it was a {@link AttemptsAggregate#emptySuccess()}.
     *
     * @param successConsumer The consumer to process the count of successful attempts.
     * @return This {@link AttemptsAggregate} instance for method chaining.
     */
    public AttemptsAggregate<T, F> onSuccessCount(Consumer<Integer> successConsumer) {
        if (hasSuccess()) {
            successConsumer.accept(successAttempts.size());
        }
        return this;
    }

    /**
     * Executes the provided {@link Consumer} with a map of failure reasons and their occurrence counts if any failures exist.
     *
     * @param failureConsumer The consumer to process the failure reasons count.
     * @return This {@link AttemptsAggregate} instance for method chaining.
     */
    public AttemptsAggregate<T, F> onFailureCount(Consumer<Map<F, Long>> failureConsumer) {
        if (hasFailure()) {
            failureConsumer.accept(failureAttempts.stream()
                    .collect(Collectors.groupingBy(Attempt::getFailureReason, Collectors.counting())));
        }
        return this;
    }

    /**
     * Checks if there are no failed attempts or has at least one successful attempt.
     *
     * @return {@code true} if there are successful attempts, {@code false} otherwise.
     */
    public boolean hasSuccess() {
        return failureAttempts.isEmpty() || !successAttempts.isEmpty();
    }

    /**
     * Checks if there are any failed attempts.
     *
     * @return {@code true} if there are failed attempts, {@code false} otherwise.
     */
    public boolean hasFailure() {
        return !failureAttempts.isEmpty();
    }
}
