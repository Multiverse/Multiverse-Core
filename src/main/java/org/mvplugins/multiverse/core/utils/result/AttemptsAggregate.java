package org.mvplugins.multiverse.core.utils.result;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class AttemptsAggregate<T, F extends FailureReason> {

    public static <T, F extends FailureReason> AttemptsAggregate<T, F> allOf(List<Attempt<T, F>> attempts) {
        return new AttemptsAggregate<>(attempts);
    }

    public static <T, F extends FailureReason> AttemptsAggregate<T, F> allOf(Attempt<T, F>... attempts) {
        return new AttemptsAggregate<>(List.of(attempts));
    }

    public static <T, F extends FailureReason> AttemptsAggregate<T, F> emptySuccess() {
        return new AttemptsAggregate<>(Collections.emptyList());
    }

    private final List<Attempt<T, F>> successAttempts;
    private final List<Attempt<T, F>> failureAttempts;

    private AttemptsAggregate(List<Attempt<T, F>> attempts) {
        successAttempts = attempts.stream().filter(Attempt::isSuccess).toList();
        failureAttempts = attempts.stream().filter(Attempt::isFailure).toList();
    }

    public AttemptsAggregate<T, F> onSuccess(Consumer<List<Attempt<T, F>>> successConsumer) {
        if (!failureAttempts.isEmpty()) {
            return this;
        }
        successConsumer.accept(successAttempts);
        return this;
    }

    public AttemptsAggregate<T, F> onFailure(Consumer<List<Attempt<T, F>>> failureConsumer) {
        if (failureAttempts.isEmpty()) {
            return this;
        }
        failureConsumer.accept(failureAttempts);
        return this;
    }

    public AttemptsAggregate<T, F> onSuccessCount(Consumer<Integer> successConsumer) {
        if (!failureAttempts.isEmpty()) {
            return this;
        }
        successConsumer.accept(successAttempts.size());
        return this;
    }

    public AttemptsAggregate<T, F> onFailureCount(Consumer<Map<F, Long>> failureConsumer) {
        if (failureAttempts.isEmpty()) {
            return this;
        }
        failureConsumer.accept(failureAttempts.stream()
                .collect(Collectors.groupingBy(Attempt::getFailureReason, Collectors.counting())));
        return this;
    }
}

