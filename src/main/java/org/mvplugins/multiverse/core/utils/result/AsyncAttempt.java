package org.mvplugins.multiverse.core.utils.result;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.mvplugins.multiverse.core.utils.message.MessageReplacement;

public final class AsyncAttempt<T, F extends FailureReason> {

    public static <T, F extends FailureReason> Async<List<Attempt<T, F>>> allOf(List<AsyncAttempt<T, F>> attempts) {
        return Async.of(CompletableFuture.allOf(attempts.stream()
                .map(attempt -> attempt.future)
                .toArray(CompletableFuture[]::new))
                .thenApply(v -> attempts.stream()
                        .map(attempt -> attempt.future.join())
                        .toList()));
    }

    public static <T, F extends FailureReason> AsyncAttempt<T, F> of(
            CompletableFuture<T> future,
            BiFunction<T, Throwable, Attempt<T, F>> completionHandler) {
        return new AsyncAttempt<>(future.handleAsync(completionHandler));
    }

    public static <T, F extends FailureReason> AsyncAttempt<T, F> of(
            CompletableFuture<T> future,
            Function<Throwable, Attempt<T, F>> exceptionHandler) {
        BiFunction<T, Throwable, Attempt<T, F>> completionHandler = (result, exception) -> {
            if (exception != null) {
                return exceptionHandler.apply(exception);
            } else {
                return Attempt.success(result);
            }
        };
        return of(future, completionHandler);
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

    public <U> AsyncAttempt<U, F> map(Function<? super T, ? extends U> mapper) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.map(mapper)));
    }

    public <U> AsyncAttempt<U, F> mapAsyncAttempt(Function<? super T, AsyncAttempt<U, F>> mapper) {
        return new AsyncAttempt<>(future.thenApplyAsync(
                attempt -> attempt.mapAttempt(rasult -> mapper.apply(rasult).toAttempt())));
    }

    public AsyncAttempt<T, F> onSuccess(Runnable runnable) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.onSuccess(runnable)));
    }

    public Attempt<T, F> toAttempt() {
        return future.join();
    }
}
