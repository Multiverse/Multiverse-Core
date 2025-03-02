package org.mvplugins.multiverse.core.utils.result;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents the result of an asynchronous operation that wraps a {@link CompletableFuture}.
 *
 * @param <T>   The type of the value.
 */
public final class Async<T> {

    /**
     * Returns a new AsyncResult that is completed when all of the given AsyncResult complete.
     *
     * @param results   The results to wait for.
     * @return A new AsyncResult that is completed when all of the given AsyncResult complete.
     */
    public static Async<Void> allOf(Async<?>... results) {
        return new Async<>(CompletableFuture.allOf(Arrays.stream(results)
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
    public static <T> Async<List<T>> allOf(List<Async<T>> results) {
        return new Async<>(CompletableFuture.allOf(results.stream()
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
    public static <T> Async<T> of(CompletableFuture<T> future) {
        return new Async<>(future);
    }

    /**
     * Returns a new AsyncResult that is already completed with the given value.
     *
     * @param value The value to complete the AsyncResult with.
     * @param <T>   The type of the value.
     * @return The completed AsyncResult.
     */
    public static <T> Async<T> completedFuture(T value) {
        return new Async<>(CompletableFuture.completedFuture(value));
    }

    /**
     * Returns a new CompletableFuture that is already completed exceptionally with the given exception.
     *
     * @param throwable The exception to complete the AsyncResult with.
     * @param <T>       The type of the value.
     * @return The completed AsyncResult.
     */
    public static <T> Async<T> failedFuture(Throwable throwable) {
        return new Async<>(CompletableFuture.failedFuture(throwable));
    }

    private final CompletableFuture<T> future;

    private Async(CompletableFuture<T> future) {
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
    public Async<Void> thenAccept(Consumer<T> consumer) {
        return new Async<>(future.thenAccept(consumer));
    }

    /**
     * Executes the given action when this AsyncResult completes.
     *
     * @param runnable  The action to perform.
     * @return This AsyncResult.
     */
    public Async<Void> thenRun(Runnable runnable) {
        return new Async<>(future.thenRun(runnable));
    }

    /**
     * Executes the given action when this AsyncResult completes and returns a new AsyncResult with the new value.
     *
     * @param function  The action to perform.
     * @param <U>       The type of the new value.
     * @return A new AsyncResult with the new value.
     */
    public <U> Async<U> thenApply(Function<T, U> function) {
        return new Async<>(future.thenApply(function));
    }

    /**
     * Executes the given action when this AsyncResult completes with an exception.
     *
     * @param consumer  The action to perform.
     * @return This AsyncResult.
     */
    public Async<T> exceptionally(Consumer<Throwable> consumer) {
        return new Async<>(future.exceptionally(throwable -> {
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
    public Async<T> exceptionally(Function<Throwable, T> function) {
        return new Async<>(future.exceptionally(function));
    }
}
