package org.mvplugins.multiverse.core.utils.result;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public final class AsyncResult<T> {

    /**
     * Returns a new AsyncResult that is completed when all of the given AsyncResult complete.
     *
     * @param results   The results to wait for.
     * @return A new AsyncResult that is completed when all of the given AsyncResult complete.
     */
    public static AsyncResult<Void> allOf(AsyncResult<?>... results) {
        return new AsyncResult<>(CompletableFuture.allOf(Arrays.stream(results)
                .map(result -> result.future)
                .toArray(CompletableFuture[]::new)));
    }

    /**
     * Returns a new AsyncResult that is completed when all of the given AsyncResult with the same type complete.
     *
     * @param results   The results to wait for.
     * @param <T>       The type of the AsyncResult.
     * @return A new AsyncResult that is completed when all of the given AsyncResult complete.
     */
    public static <T> AsyncResult<List<T>> allOf(List<AsyncResult<T>> results) {
        return new AsyncResult<>(CompletableFuture.allOf(results.stream()
                .map(result -> result.future)
                .toArray(CompletableFuture[]::new))
                .thenApply(v -> results.stream()
                        .map(result -> result.future.join())
                        .toList()));
    }

    /**
     * Wraps a CompletableFuture in an AsyncResult.
     *
     * @param future    The future to wrap.
     * @param <T>       The type of the future.
     * @return A new AsyncResult that is completed when the given future completes.
     */
    public static <T> AsyncResult<T> of(CompletableFuture<T> future) {
        return new AsyncResult<>(future);
    }

    /**
     * Returns a new AsyncResult that is already completed with the given value.
     *
     * @param value The value to complete the AsyncResult with.
     * @param <T>   The type of the value.
     * @return The completed AsyncResult.
     */
    public static <T> AsyncResult<T> completedFuture(T value) {
        return new AsyncResult<>(CompletableFuture.completedFuture(value));
    }

    /**
     * Returns a new CompletableFuture that is already completed exceptionally with the given exception.
     *
     * @param throwable The exception to complete the AsyncResult with.
     * @param <T>       The type of the value.
     * @return The completed AsyncResult.
     */
    public static <T> AsyncResult<T> failedFuture(Throwable throwable) {
        return new AsyncResult<>(CompletableFuture.failedFuture(throwable));
    }

    private final CompletableFuture<T> future;

    /**
     * Creates a new AsyncResult.
     */
    public AsyncResult() {
        this(new CompletableFuture<>());
    }

    private AsyncResult(CompletableFuture<T> future) {
        this.future = future;
    }

    /**
     * If not already completed, sets the value returned by related methods to the given value.
     *
     * @param value The value to complete the AsyncResult with.
     * @return true if this invocation caused this AsyncResult to transition to a completed state, else false.
     */
    public boolean complete(T value) {
        return future.complete(value);
    }

    /**
     * If not already completed, causes invocations of related methods to throw the given exception.
     *
     * @param throwable The exception to complete the AsyncResult with.
     * @return true if this invocation caused this AsyncResult to transition to a completed state, else false.
     */
    public boolean completeExceptionally(Throwable throwable) {
        return future.completeExceptionally(throwable);
    }

    /**
     * Executes the given action when this AsyncResult completes.
     *
     * @param consumer  The action to perform.
     * @return This AsyncResult.
     */
    public AsyncResult<Void> thenAccept(Consumer<T> consumer) {
        return new AsyncResult<>(future.thenAccept(consumer));
    }

    /**
     * Executes the given action when this AsyncResult completes.
     *
     * @param runnable  The action to perform.
     * @return This AsyncResult.
     */
    public AsyncResult<Void> thenRun(Runnable runnable) {
        return new AsyncResult<>(future.thenRun(runnable));
    }

    /**
     * Executes the given action when this AsyncResult completes and returns a new AsyncResult with the new value.
     *
     * @param function  The action to perform.
     * @param <U>       The type of the new value.
     * @return A new AsyncResult with the new value.
     */
    public <U> AsyncResult<U> thenApply(Function<T, U> function) {
        return new AsyncResult<>(future.thenApply(function));
    }

    /**
     * Executes the given action when this AsyncResult completes with an exception.
     *
     * @param consumer  The action to perform.
     * @return This AsyncResult.
     */
    public AsyncResult<T> exceptionally(Consumer<Throwable> consumer) {
        return new AsyncResult<>(future.exceptionally(throwable -> {
            consumer.accept(throwable);
            return null;
        }));
    }

    /**
     * Executes the given action when this AsyncResult completes with an exception and returns a new AsyncResult with the new value.
     *
     * @param function  The action to perform.
     * @return A new AsyncResult with the new value.
     */
    public AsyncResult<T> exceptionally(Function<Throwable, T> function) {
        return new AsyncResult<>(future.exceptionally(function));
    }
}
