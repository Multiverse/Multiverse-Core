package org.mvplugins.multiverse.core.utils.result;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class AsyncAttemptsAggregate<T, F extends FailureReason> {

    public static <T, F extends FailureReason> AsyncAttemptsAggregate<T, F> allOf(List<AsyncAttempt<T, F>> attempts) {
        return new AsyncAttemptsAggregate<>(attempts);
    }

    public static <T, F extends FailureReason> AsyncAttemptsAggregate<T, F> allOf(AsyncAttempt<T, F>... attempts) {
        return new AsyncAttemptsAggregate<>(List.of(attempts));
    }

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

    public AsyncAttemptsAggregate<T, F> onSuccess(Consumer<List<Attempt<T, F>>> successConsumer) {
        return new AsyncAttemptsAggregate<>(
                future.thenApply(aggregate -> aggregate.onSuccess(successConsumer)));
    }

    public AsyncAttemptsAggregate<T, F> onFailure(Consumer<List<Attempt<T, F>>> failureConsumer) {
        return new AsyncAttemptsAggregate<>(
                future.thenApply(aggregate -> aggregate.onFailure(failureConsumer)));
    }

    public AsyncAttemptsAggregate<T, F> onSuccessCount(Consumer<Integer> successConsumer) {
        return new AsyncAttemptsAggregate<>(
                future.thenApply(aggregate -> aggregate.onSuccessCount(successConsumer)));
    }

    public AsyncAttemptsAggregate<T, F> onFailureCount(Consumer<Map<F, Long>> failureConsumer) {
        return new AsyncAttemptsAggregate<>(
                future.thenApply(aggregate -> aggregate.onFailureCount(failureConsumer)));
    }
}
