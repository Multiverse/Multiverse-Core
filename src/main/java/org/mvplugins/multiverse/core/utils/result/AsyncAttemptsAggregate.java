package org.mvplugins.multiverse.core.utils.result;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * An asynchronous wrapper for {@link AttemptsAggregate}.
 * This class aggregates a list of {@link AsyncAttempt} objects, ensuring all futures are completed
 * before converting them into a synchronous {@link AttemptsAggregate}.
 *
 * @param <T> The type of the successful result.
 * @param <F> The type representing failure reasons.
 */
public final class AsyncAttemptsAggregate<T, F extends FailureReason> {

    /**
     * Creates an {@link AsyncAttemptsAggregate} from a list of asynchronous attempts.
     *
     * @param attempts The list of asynchronous attempts to aggregate.
     * @param <T> The type of the successful result.
     * @param <F> The type representing failure reasons.
     * @return An instance of {@link AsyncAttemptsAggregate}.
     */
    public static <T, F extends FailureReason> AsyncAttemptsAggregate<T, F> allOf(List<AsyncAttempt<T, F>> attempts) {
        return new AsyncAttemptsAggregate<>(attempts);
    }

    /**
     * Creates an {@link AsyncAttemptsAggregate} from a varargs array of asynchronous attempts.
     *
     * @param attempts The asynchronous attempts to aggregate.
     * @param <T> The type of the successful result.
     * @param <F> The type representing failure reasons.
     * @return An instance of {@link AsyncAttemptsAggregate}.
     */
    public static <T, F extends FailureReason> AsyncAttemptsAggregate<T, F> allOf(AsyncAttempt<T, F>... attempts) {
        return new AsyncAttemptsAggregate<>(List.of(attempts));
    }

    /**
     * Creates an {@link AsyncAttemptsAggregate} representing an empty successful state.
     *
     * @param <T> The type of the successful result.
     * @param <F> The type representing failure reasons.
     * @return An instance of {@link AsyncAttemptsAggregate} with no attempts.
     */
    public static <T, F extends FailureReason> AsyncAttemptsAggregate<T, F> emptySuccess() {
        return new AsyncAttemptsAggregate<>(CompletableFuture.completedFuture(AttemptsAggregate.emptySuccess()));
    }

    private final CompletableFuture<AttemptsAggregate<T, F>> future;

    private AsyncAttemptsAggregate(List<AsyncAttempt<T, F>> attempts) {
        future = CompletableFuture.allOf(attempts.stream().map(AsyncAttempt::getFuture).toArray(CompletableFuture[]::new))
                .thenApply(v -> AttemptsAggregate.allOf(attempts.stream()
                        .map(AsyncAttempt::getFuture)
                        .map(CompletableFuture::join).toList()));
    }

    private AsyncAttemptsAggregate(CompletableFuture<AttemptsAggregate<T, F>> future) {
        this.future = future;
    }

    /**
     * Executes the provided {@link Runnable} if there are successful attempts.
     *
     * @param runnable The action to execute on success.
     * @return A new {@link AsyncAttemptsAggregate} instance.
     */
    public AsyncAttemptsAggregate<T, F> onSuccess(Runnable runnable) {
        return new AsyncAttemptsAggregate<>(
                future.thenApply(aggregate -> aggregate.onSuccess(runnable)));
    }

    /**
     * Executes the provided {@link Runnable} if there are failed attempts.
     *
     * @param runnable The action to execute on failure.
     * @return A new {@link AsyncAttemptsAggregate} instance.
     */
    public AsyncAttemptsAggregate<T, F> onFailure(Runnable runnable) {
        return new AsyncAttemptsAggregate<>(
                future.thenApply(aggregate -> aggregate.onFailure(runnable)));
    }

    /**
     * Executes the provided {@link Consumer} with the list of successful attempts if any exist.
     *
     * @param successConsumer The consumer to process successful attempts.
     * @return A new {@link AsyncAttemptsAggregate} instance.
     */
    public AsyncAttemptsAggregate<T, F> onSuccess(Consumer<List<Attempt<T, F>>> successConsumer) {
        return new AsyncAttemptsAggregate<>(
                future.thenApply(aggregate -> aggregate.onSuccess(successConsumer)));
    }

    /**
     * Executes the provided {@link Consumer} with the list of failed attempts if any exist.
     *
     * @param failureConsumer The consumer to process failed attempts.
     * @return A new {@link AsyncAttemptsAggregate} instance.
     */
    public AsyncAttemptsAggregate<T, F> onFailure(Consumer<List<Attempt<T, F>>> failureConsumer) {
        return new AsyncAttemptsAggregate<>(
                future.thenApply(aggregate -> aggregate.onFailure(failureConsumer)));
    }

    /**
     * Executes the provided {@link Consumer} with the count of successful attempts if any exist.
     *
     * @param successConsumer The consumer to process the count of successful attempts.
     * @return A new {@link AsyncAttemptsAggregate} instance.
     */
    public AsyncAttemptsAggregate<T, F> onSuccessCount(Consumer<Integer> successConsumer) {
        return new AsyncAttemptsAggregate<>(
                future.thenApply(aggregate -> aggregate.onSuccessCount(successConsumer)));
    }

    /**
     * Executes the provided {@link Consumer} with a map of failure reasons and their occurrence counts if any failures exist.
     *
     * @param failureConsumer The consumer to process the failure reasons count.
     * @return A new {@link AsyncAttemptsAggregate} instance.
     */
    public AsyncAttemptsAggregate<T, F> onFailureCount(Consumer<Map<F, Long>> failureConsumer) {
        return new AsyncAttemptsAggregate<>(
                future.thenApply(aggregate -> aggregate.onFailureCount(failureConsumer)));
    }
}
