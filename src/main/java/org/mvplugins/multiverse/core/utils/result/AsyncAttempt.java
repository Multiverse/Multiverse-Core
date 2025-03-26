package org.mvplugins.multiverse.core.utils.result;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import io.vavr.control.Either;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;

/**
 * An asynchronous wrapper for the {@link Attempt} class.
 * <p>
 * This class represents an attempt to process a value asynchronously, where the operation can either succeed
 * with a result of type {@code T} or fail with a reason of type {@code F}.
 *
 * @param <T> The type of the successful result.
 * @param <F> The type representing failure reasons.
 */
public final class AsyncAttempt<T, F extends FailureReason> {

    /**
     * Wraps a {@link CompletableFuture} into an {@link AsyncAttempt}, using a completion handler to determine success or failure.
     *
     * @param future The future to wrap.
     * @param completionHandler A function that processes the result or exception and returns an {@link Attempt}.
     * @param <T> The type of the successful result.
     * @param <F> The type representing failure reasons.
     * @return An instance of {@link AsyncAttempt}.
     */
    public static <T, F extends FailureReason> AsyncAttempt<T, F> of(
            CompletableFuture<T> future,
            BiFunction<T, Throwable, Attempt<T, F>> completionHandler) {
        return new AsyncAttempt<>(future.handle(completionHandler));
    }

    /**
     * Wraps a {@link CompletableFuture} into an {@link AsyncAttempt}, using an exception handler to determine failure cases.
     *
     * @param future The future to wrap.
     * @param exceptionHandler A function that processes exceptions and returns an {@link Attempt}.
     * @param <T> The type of the successful result.
     * @param <F> The type representing failure reasons.
     * @return An instance of {@link AsyncAttempt}.
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
     * Creates an {@link AsyncAttempt} from an existing {@link Attempt}, wrapping it in a completed {@link CompletableFuture}.
     *
     * @param attempt The attempt to wrap.
     * @param <T> The type of the successful result.
     * @param <F> The type representing failure reasons.
     * @return An instance of {@link AsyncAttempt}.
     */
    public static <T, F extends FailureReason> AsyncAttempt<T, F> fromAttempt(Attempt<T, F> attempt) {
        return new AsyncAttempt<>(CompletableFuture.completedFuture(attempt));
    }

    /**
     * Creates a successful {@link AsyncAttempt} with a {@code null} result.
     *
     * @param <F> The type representing failure reasons.
     * @return A successful {@link AsyncAttempt}.
     */
    public static <F extends FailureReason> AsyncAttempt<Void, F> success() {
        return new AsyncAttempt<>(CompletableFuture.completedFuture(null));
    }

    /**
     * Creates a failed {@link AsyncAttempt} with a specified failure reason and optional message replacements.
     *
     * @param failureReason The reason for failure.
     * @param messageReplacements Optional message replacements for localization.
     * @param <F> The type representing failure reasons.
     * @return A failed {@link AsyncAttempt}.
     */
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

    /**
     * Executes the provided runnable with the result of this {@link AsyncAttempt} if it is successful.
     *
     * @param runnable  The runnable to process the result or failure reason.
     * @return A new {@link AsyncAttempt} instance.
     */
    public AsyncAttempt<T, F> thenRun(Runnable runnable) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.thenRun(runnable)));
    }


    /**
     * Executes the provided consumer with the result of this {@link AsyncAttempt} if it is successful.
     *
     * @param consumer The consumer to process the result or failure reason.
     * @return A new {@link AsyncAttempt} instance.
     */
    public AsyncAttempt<T, F> thenAccept(Consumer<Either<T, F>> consumer) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.thenAccept(consumer)));
    }

    /**
     * Transforms the successful result of this {@link AsyncAttempt} into a new successful result using the provided mapper function.
     * If the current attempt is a failure, the failure reason and message are propagated to the new attempt.
     *
     * @param mapper The function to transform the successful result into a new result.
     * @param <U> The type of the new result.
     * @return A new {@link AsyncAttempt} containing the transformed result.
     */
    public <U> AsyncAttempt<U, F> map(Function<? super T, ? extends U> mapper) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.map(mapper)));
    }

    /**
     * Transforms the result of this {@link AsyncAttempt} into a new attempt using the provided mapper function
     * if it is successful.
     *
     * @param mapper The function to transform the successful result into a new {@link Attempt}.
     * @param <U> The type of the result in the new attempt.
     * @return A new {@link AsyncAttempt} containing the transformed result.
     */
    public <U> AsyncAttempt<U, F> mapAttempt(Function<? super T, Attempt<U, F>> mapper) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.mapAttempt(mapper)));
    }

    /**
     * Transforms the successful result of this {@link AsyncAttempt} into another asynchronous attempt.
     * Note the function will not run on the main thread, so do not modify non thread-safe bukkit apis.
     *
     * @param mapper The function that maps the result into another {@link AsyncAttempt}.
     * @param <U> The type of the transformed result.
     * @return A new {@link AsyncAttempt} containing the transformed result.
     */
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

    /**
     * Executes a runnable if this {@link AsyncAttempt} is successful.
     *
     * @param runnable The action to execute on success.
     * @return A new {@link AsyncAttempt} instance.
     */
    public AsyncAttempt<T, F> onSuccess(Runnable runnable) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.onSuccess(runnable)));
    }

    /**
     * Executes a runnable if this {@link AsyncAttempt} fails.
     *
     * @param runnable The action to execute on failure.
     * @return A new {@link AsyncAttempt} instance.
     */
    public AsyncAttempt<T, F> onFailure(Runnable runnable) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.onFailure(runnable)));
    }

    /**
     * Executes a consumer with the {@link Attempt.Failure} if this {@link AsyncAttempt} fails.
     *
     * @param consumer The consumer to process the failure.
     * @return A new {@link AsyncAttempt} instance.
     */
    public AsyncAttempt<T, F> onFailure(Consumer<Attempt.Failure<T, F>> consumer) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.onFailure(consumer)));
    }

    /**
     * Executes a consumer with the failure reason if this {@link AsyncAttempt} fails.
     *
     * @param consumer The consumer to process the failure reason.
     * @return A new {@link AsyncAttempt} instance.
     */
    public AsyncAttempt<T, F> onFailureReason(Consumer<F> consumer) {
        return new AsyncAttempt<>(future.thenApply(attempt -> attempt.onFailureReason(consumer)));
    }

    /**
     * Blocks until the asynchronous attempt completes and returns the corresponding {@link Attempt}.
     *
     * @return The resolved {@link Attempt}.
     */
    public Attempt<T, F> toAttempt() {
        return future.join();
    }

    CompletableFuture<Attempt<T, F>> getFuture() {
        return future;
    }
}
