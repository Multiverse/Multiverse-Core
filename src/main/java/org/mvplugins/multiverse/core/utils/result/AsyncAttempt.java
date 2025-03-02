package org.mvplugins.multiverse.core.utils.result;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import io.vavr.control.Either;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;

/**
 *
 *
 * @param <T>   The success value type.
 * @param <F>   The failure reason type.
 */
public final class AsyncAttempt<T, F extends FailureReason> {

    /**
     * From a list of attempts.
     *
     * @param attempts
     * @return
     * @param <T>
     * @param <F>
     */
    public static <T, F extends FailureReason> Async<List<Attempt<T, F>>> allOf(List<AsyncAttempt<T, F>> attempts) {
        return Async.of(CompletableFuture.allOf(attempts.stream()
                .map(attempt -> attempt.future)
                .toArray(CompletableFuture[]::new))
                .thenApply(v -> attempts.stream()
                        .map(attempt -> attempt.future.join())
                        .toList()));
    }

    /**
     * Create from a completable future.
     *
     * @param future
     * @param completionHandler
     * @return
     * @param <T>
     * @param <F>
     */
    public static <T, F extends FailureReason> AsyncAttempt<T, F> of(
            CompletableFuture<T> future,
            BiFunction<T, Throwable, Attempt<T, F>> completionHandler) {
        return new AsyncAttempt<>(future.handle(completionHandler));
    }

    /**
     *
     * @param future
     * @param exceptionHandler
     * @return
     * @param <T>
     * @param <F>
     */
    public static <T, F extends FailureReason> AsyncAttempt<T, F> of(
            CompletableFuture<T> future,
            Function<Throwable, Attempt<T, F>> exceptionHandler) {
        BiFunction<T, Throwable, Attempt<T, F>> completionHandler = (result, exception) -> exception != null
                ? exceptionHandler.apply(exception)
                : Attempt.success(result);
        return of(future, completionHandler);
    }

    /**
     *
     * @param attempt
     * @return
     * @param <T>
     * @param <F>
     */
    public static <T, F extends FailureReason> AsyncAttempt<T, F> fromAttempt(Attempt<T, F> attempt) {
        return new AsyncAttempt<>(CompletableFuture.completedFuture(attempt));
    }

    public static <F extends FailureReason> AsyncAttempt<Void, F> success() {
        return new AsyncAttempt<>(CompletableFuture.completedFuture(null));
    }

    public static <F extends FailureReason> AsyncAttempt<Void, F> failure(
            F failureReason,
            MessageReplacement... messageReplacements) {
        return new AsyncAttempt<>(CompletableFuture.completedFuture(
                Attempt.failure(failureReason, messageReplacements)));
    }

    private final CompletableFuture<Attempt<T, F>> future;

    private AsyncAttempt(CompletableFuture<Attempt<T, F>> future) {
        this.future = future;
    }

    public AsyncAttempt<T, F> thenRun(Runnable runnable) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.thenRun(runnable)));
    }

    public AsyncAttempt<T, F> thenAccept(Consumer<Either<T, F>> consumer) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.thenAccept(consumer)));
    }

    public <U> AsyncAttempt<U, F> map(Function<? super T, ? extends U> mapper) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.map(mapper)));
    }

    public <U> AsyncAttempt<U, F> mapAttempt(Function<? super T, Attempt<U, F>> mapper) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.mapAttempt(mapper)));
    }

    public <U> AsyncAttempt<U, F> mapAsyncAttempt(Function<? super T, AsyncAttempt<U, F>> mapper) {
        return new AsyncAttempt<>(future.thenCompose(attempt -> {
            if (attempt.isSuccess()) {
                return mapper.apply(attempt.get()).future;
            } else {
                return CompletableFuture.completedFuture(
                        new Attempt.Failure<>(attempt.getFailureReason(), attempt.getFailureMessage()));
            }
        }));
    }

    public AsyncAttempt<T, F> onSuccess(Runnable runnable) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.onSuccess(runnable)));
    }

    public AsyncAttempt<T, F> onFailure(Runnable runnable) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.onFailure(runnable)));
    }

    public AsyncAttempt<T, F> onFailure(Consumer<Attempt.Failure<T, F>> consumer) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.onFailure(consumer)));
    }

    public AsyncAttempt<T, F> onFailureReason(Consumer<F> consumer) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.onFailureReason(consumer)));
    }

    public Attempt<T, F> toAttempt() {
        return future.join();
    }
}
