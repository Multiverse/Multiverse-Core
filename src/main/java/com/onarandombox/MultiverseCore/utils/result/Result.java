package com.onarandombox.MultiverseCore.utils.result;

import com.onarandombox.MultiverseCore.utils.message.Message;
import com.onarandombox.MultiverseCore.utils.message.MessageReplacement;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Result<T, S extends SuccessReason, F extends FailureReason> {

    static <T, S extends SuccessReason, F extends FailureReason> Result<T, S, F> success() {
        return new Success<>(null, null, Message.of("Success!"));
    }

    static <T, S extends SuccessReason, F extends FailureReason> Result<T, S, F> success(
            S successReason, MessageReplacement... messageReplacements) {
        return new Success<>(null, successReason, Message.of(successReason, "Success!", messageReplacements));
    }

    static <T, S extends SuccessReason, F extends FailureReason> Result<T, S, F> success(
            S successReason, Message message) {
        return new Success<>(null, successReason, message);
    }

    static <T, S extends SuccessReason, F extends FailureReason> Result<T, S, F> successValue(T value) {
        return new Success<>(value, null, Message.of("Success!"));
    }

    static <T, S extends SuccessReason, F extends FailureReason> Result<T, S, F> successValue(
            T value, S successReason, MessageReplacement... messageReplacements) {
        return new Success<>(value, successReason, Message.of(successReason, "Success!", messageReplacements));
    }

    static <T, S extends SuccessReason, F extends FailureReason> Result<T, S, F> successValue(
            T value, S successReason, Message message) {
        return new Success<>(value, successReason, message);
    }

    static <T, S extends SuccessReason, F extends FailureReason> Result<T, S, F> failure() {
        return new Failure<>(null, Message.of("Failed!"));
    }

    static <T, S extends SuccessReason, F extends FailureReason> Result<T, S, F> failure(
            F failureReason, MessageReplacement... messageReplacements) {
        return new Failure<>(failureReason, Message.of(failureReason, "Failed!", messageReplacements));
    }

    static <T, S extends SuccessReason, F extends FailureReason> Result<T, S, F> failure(
            F failureReason, Message message) {
        return new Failure<>(failureReason, message);
    }

    T getValue();

    S getSuccessReason();

    F getFailureReason();

    Message getMessage();

    default boolean isSuccess() {
        return this instanceof Success;
    }

    default boolean isFailure() {
        return this instanceof Failure;
    }

    default Result<T, S, F> peek(Consumer<T> consumer) {
        if (this instanceof Success) {
            consumer.accept(getValue());
        }
        return this;
    }

    default <N> Result<N, S, F> map(Function<Success<T, S, F>, Result<N, S, F>> mapper) {
        if (this instanceof Success) {
            return mapper.apply((Success<T, S, F>) this);
        } else {
            return new Failure<>(getFailureReason(), getMessage());
        }
    }

    default <N> Result<N, S, F> map(Supplier<Result<N, S, F>> mapper) {
        if (this instanceof Success) {
            return mapper.get();
        } else {
            return new Failure<>(getFailureReason(), getMessage());
        }
    }

    default <N> Result<N, S, F> mapValue(Function<T, Result<N, S, F>> mapper) {
        if (this instanceof Success) {
            return mapper.apply(this.getValue());
        } else {
            return new Failure<>(getFailureReason(), getMessage());
        }
    }

    default <NS extends SuccessReason, NF extends FailureReason> Result<T, NS, NF> convertReason(
            NS success, NF failure) {
        if (this instanceof Success) {
            return new Success<>(getValue(), success, getMessage());
        } else {
            return new Failure<>(failure, getMessage());
        }
    }

    default <N> N fold(Function<Failure<T, S, F>, N> failureMapper, Function<Success<T, S, F>, N> successMapper) {
        if (this instanceof Success) {
            return successMapper.apply((Success<T, S, F>) this);
        } else {
            return failureMapper.apply((Failure<T, S, F>) this);
        }
    }

    default Result<T, S, F> onSuccess(Runnable runnable) {
        if (this instanceof Success) {
            runnable.run();
        }
        return this;
    }

    default Result<T, S, F> onSuccess(Consumer<Success<T, S, F>> consumer) {
        if (this instanceof Success) {
            consumer.accept((Success<T, S, F>) this);
        }
        return this;
    }

    default Result<T, S, F> onSuccessReason(S successReason, Runnable runnable) {
        if (this instanceof Success && this.getSuccessReason() == successReason) {
            runnable.run();
        }
        return this;
    }

    default Result<T, S, F> onSuccessValue(Consumer<T> consumer) {
        if (this instanceof Success) {
            consumer.accept(getValue());
        }
        return this;
    }

    default Result<T, S, F> onSuccessReason(Consumer<S> consumer) {
        if (this instanceof Success) {
            consumer.accept(getSuccessReason());
        }
        return this;
    }

    default Result<T, S, F> onFailure(Runnable runnable) {
        if (this instanceof Failure) {
            runnable.run();
        }
        return this;
    }

    default Result<T, S, F> onFailure(Consumer<Failure<T, S, F>> consumer) {
        if (this instanceof Failure) {
            consumer.accept((Failure<T, S, F>) this);
        }
        return this;
    }

    default Result<T, S, F> onFailureReason(Consumer<F> consumer) {
        if (this instanceof Failure) {
            consumer.accept(getFailureReason());
        }
        return this;
    }

    class Success<T, S extends SuccessReason, F extends FailureReason> implements Result<T, S, F> {
        private final T value;
        private final S successReason;
        private final Message message;

        Success(T value, S successReason, Message message) {
            this.value = value;
            this.successReason = successReason;
            this.message = message;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public S getSuccessReason() {
            return successReason;
        }

        @Override
        public F getFailureReason() {
            return null;
        }

        @Override
        public Message getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Success{"
                    + "reason=" + successReason + ", "
                    + "value=" + value
                    + '}';
        }
    }

    class Failure<T, S extends SuccessReason, F extends FailureReason> implements Result<T, S, F> {
        private final F failureReason;
        private final Message message;

        Failure(F failureReason, Message message) {
            this.failureReason = failureReason;
            this.message = message;
        }

        @Override
        public T getValue() {
            return null;
        }

        @Override
        public S getSuccessReason() {
            return null;
        }

        @Override
        public F getFailureReason() {
            return failureReason;
        }

        @Override
        public Message getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Failure{"
                    + "reason=" + failureReason
                    + '}';
        }
    }
}
