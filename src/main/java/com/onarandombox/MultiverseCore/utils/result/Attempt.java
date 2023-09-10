package com.onarandombox.MultiverseCore.utils.result;

import com.onarandombox.MultiverseCore.utils.message.Message;
import com.onarandombox.MultiverseCore.utils.message.MessageReplacement;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Attempt<T, F extends FailureReason> {

    static <T, F extends FailureReason> Attempt<T, F> success(T value) {
        return new Success<>(value);
    }

    static <T, F extends FailureReason> Attempt<T, F> failure(
            F failureReason, MessageReplacement... messageReplacements) {
        return new Failure<>(failureReason, Message.of(failureReason, "Failed!", messageReplacements));
    }

    static <T, F extends FailureReason> Attempt<T, F> failure(F failureReason, Message message) {
        return new Failure<>(failureReason, message);
    }

    T get();

    F getFailureReason();

    Message getFailureMessage();

    default boolean isSuccess() {
        return this instanceof Success;
    }

    default boolean isFailure() {
        return this instanceof Failure;
    }

    default Attempt<T, F> peek(Consumer<T> consumer) {
        if (this instanceof Success) {
            consumer.accept(get());
        }
        return this;
    }

    default <U> Attempt<U, F> map(Function<? super T, ? extends U> mapper) {
        if (this instanceof Success) {
            return new Success<>(mapper.apply(get()));
        } else {
            return new Failure<>(getFailureReason(), getFailureMessage());
        }
    }

    default <U> Attempt<U, F> map(Supplier<? extends U> mapper) {
        if (this instanceof Success) {
            return new Success<>(mapper.get());
        } else {
            return new Failure<>(getFailureReason(), getFailureMessage());
        }
    }

    default <U> Attempt<U, F> mapAttempt(Function<? super T, Attempt<U, F>> mapper) {
        if (this instanceof Success) {
            return mapper.apply(get());
        } else {
            return new Failure<>(getFailureReason(), getFailureMessage());
        }
    }

    default <U> Attempt<U, F> mapAttempt(Supplier<Attempt<U, F>> mapper) {
        if (this instanceof Success) {
            return mapper.get();
        } else {
            return new Failure<>(getFailureReason(), getFailureMessage());
        }
    }

    default <UF extends FailureReason> Attempt<T, UF> transform(UF failureReason) {
        if (this instanceof Success) {
            return new Success<>(get());
        } else {
            return new Failure<>(failureReason, getFailureMessage());
        }
    }

    default <NF extends FailureReason> Attempt<T, NF> convertReason(NF failure) {
        if (this instanceof Success) {
            return new Success<>(get());
        } else {
            return new Failure<>(failure, getFailureMessage());
        }
    }

    default <N> N fold(Function<Failure<T, F>, N> failureMapper, Function<T, N> successMapper) {
        if (this instanceof Success) {
            return successMapper.apply(get());
        } else {
            return failureMapper.apply((Failure<T, F>) this);
        }
    }

    default Attempt<T, F> onSuccess(Runnable runnable) {
        if (this instanceof Success) {
            runnable.run();
        }
        return this;
    }

    default Attempt<T, F> onSuccess(Consumer<T> consumer) {
        if (this instanceof Success) {
            consumer.accept(get());
        }
        return this;
    }

    default Attempt<T, F> onFailure(Runnable runnable) {
        if (this instanceof Failure) {
            runnable.run();
        }
        return this;
    }

    default Attempt<T, F> onFailure(Consumer<Failure<T, F>> consumer) {
        if (this instanceof Failure) {
            consumer.accept((Failure<T, F>) this);
        }
        return this;
    }

    default Attempt<T, F> onFailureReason(Consumer<F> consumer) {
        if (this instanceof Failure) {
            consumer.accept(getFailureReason());
        }
        return this;
    }

    class Success<T, F extends FailureReason> implements Attempt<T, F> {
        private final T value;

        Success(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public F getFailureReason() {
            throw new IllegalStateException("Attempt is a success!");
        }

        @Override
        public Message getFailureMessage() {
            throw new IllegalStateException("Attempt is a success!");
        }

        @Override
        public String toString() {
            return "Success{"
                    + "value=" + value
                    + '}';
        }
    }

    class Failure<T, F extends FailureReason> implements Attempt<T, F> {
        private final F failureReason;
        private final Message message;

        Failure(F failureReason, Message message) {
            this.failureReason = failureReason;
            this.message = message;
        }

        @Override
        public T get() {
            throw new IllegalStateException("Attempt is a failure!");
        }

        @Override
        public F getFailureReason() {
            return failureReason;
        }

        @Override
        public Message getFailureMessage() {
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
