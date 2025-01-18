package org.mvplugins.multiverse.core.api.result;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.vavr.control.Either;
import org.mvplugins.multiverse.core.api.locale.message.Message;
import org.mvplugins.multiverse.core.api.locale.message.MessageReplacement;

/**
 * Represents an attempt to process a value that can fail with a reason that has a localized message.
 *
 * @param <T>   The type of the value.
 * @param <F>   The type of failure reason.
 * @since 5.0
 */
public sealed interface Attempt<T, F extends FailureReason> permits Attempt.Success, Attempt.Failure {

    /**
     * Creates a new success attempt.
     *
     * @param value The value.
     * @param <T>   The type of the value.
     * @param <F>   The type of failure reason.
     * @return The new success attempt.
     * @since 5.0
     */
    static <T, F extends FailureReason> Attempt<T, F> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates a new failure attempt.
     *
     * @param failureReason         The reason for failure.
     * @param messageReplacements   The replacements for the failure message.
     * @param <T>                   The type of the value.
     * @param <F>                   The type of failure reason.
     * @return The new failure attempt.
     * @since 5.0
     */
    static <T, F extends FailureReason> Attempt<T, F> failure(
            F failureReason, MessageReplacement... messageReplacements) {
        return new Failure<>(failureReason, Message.of(failureReason, "Failed!", messageReplacements));
    }

    /**
     * Creates a new failure attempt with a custom message.
     *
     * @param failureReason The reason for failure.
     * @param message       The custom message for failure. This will override the default message.
     * @param <T>           The type of the value.
     * @param <F>           The type of failure reason.
     * @return The new failure attempt.
     * @since 5.0
     */
    static <T, F extends FailureReason> Attempt<T, F> failure(F failureReason, Message message) {
        return new Failure<>(failureReason, message);
    }

    /**
     * Gets the value of this attempt. Exceptions will be thrown if this is a failure attempt.
     *
     * @return The value.
     * @since 5.0
     */
    T get();

    /**
     * Gets the reason for failure. Exceptions will be thrown if this is a success attempt.
     *
     * @return The reason for failure.
     * @since 5.0
     */
    F getFailureReason();

    /**
     * Gets the message for failure. Exceptions will be thrown if this is a success attempt.
     *
     * @return The message for failure.
     * @since 5.0
     */
    Message getFailureMessage();

    /**
     * Returns whether this attempt is a success.
     *
     * @return Whether this attempt is a success.
     * @since 5.0
     */
    default boolean isSuccess() {
        return this instanceof Success;
    }

    /**
     * Returns whether this attempt is a failure.
     *
     * @return Whether this attempt is a failure.
     * @since 5.0
     */
    default boolean isFailure() {
        return this instanceof Failure;
    }

    default Attempt<T, F> thenRun(Runnable runnable) {
        runnable.run();
        return this;
    }

    default Attempt<T, F> thenAccept(Consumer<Either<T, F>> consumer) {
        if (this instanceof Success) {
            consumer.accept(Either.left(get()));
        } else {
            consumer.accept(Either.right(getFailureReason()));
        }
        return this;
    }

    /**
     * Peeks at the value if this is a success attempt.
     *
     * @param consumer The consumer with the value.
     * @return This attempt.
     * @since 5.0
     */
    default Attempt<T, F> peek(Consumer<T> consumer) {
        if (this instanceof Success) {
            consumer.accept(get());
        }
        return this;
    }

    /**
     * Maps the value to another value if this is a success attempt.
     *
     * @param mapper    The mapper.
     * @param <U>       The type of the new value.
     * @return The new attempt.
     * @since 5.0
     */
    default <U> Attempt<U, F> map(Function<? super T, ? extends U> mapper) {
        if (this instanceof Success) {
            return new Success<>(mapper.apply(get()));
        } else {
            return new Failure<>(getFailureReason(), getFailureMessage());
        }
    }

    /**
     * Maps the value to another attempt if this is a success attempt.
     *
     * @param mapper    The mapper.
     * @param <U>       The type of the new value.
     * @return The new attempt.
     * @since 5.0
     */
    default <U> Attempt<U, F> map(Supplier<? extends U> mapper) {
        if (this instanceof Success) {
            return new Success<>(mapper.get());
        } else {
            return new Failure<>(getFailureReason(), getFailureMessage());
        }
    }

    /**
     * Maps the value to another attempt with the same fail reason if this is a success attempt.
     *
     * @param mapper    The mapper.
     * @param <U>       The type of the new value.
     * @return The new attempt.
     * @since 5.0
     */
    default <U> Attempt<U, F> mapAttempt(Function<? super T, Attempt<U, F>> mapper) {
        if (this instanceof Success) {
            return mapper.apply(get());
        } else {
            return new Failure<>(getFailureReason(), getFailureMessage());
        }
    }

    /**
     * Maps the value to another attempt with the same fail reason if this is a success attempt.
     *
     * @param mapper    The mapper.
     * @param <U>       The type of the new value.
     * @return The new attempt.
     * @since 5.0
     */
    default <U> Attempt<U, F> mapAttempt(Supplier<Attempt<U, F>> mapper) {
        if (this instanceof Success) {
            return mapper.get();
        } else {
            return new Failure<>(getFailureReason(), getFailureMessage());
        }
    }

    /**
     * Maps to another attempt with a different fail reason.
     *
     * @param failureReason The new fail reason.
     * @param <UF>          The type of the new fail reason.
     * @return The new attempt.
     * @since 5.0
     */
    default <UF extends FailureReason> Attempt<T, UF> transform(UF failureReason) {
        if (this instanceof Success) {
            return new Success<>(get());
        } else {
            return new Failure<>(failureReason, getFailureMessage());
        }
    }

    /**
     * Calls either the failure or success function depending on the result type.
     *
     * @param failureMapper The failure function.
     * @param successMapper The success function.
     * @param <N>           The type of the new value.
     * @return The result of the function.
     * @since 5.0
     */
    default <N> N fold(Function<Failure<T, F>, N> failureMapper, Function<T, N> successMapper) {
        if (this instanceof Success) {
            return successMapper.apply(get());
        } else {
            return failureMapper.apply((Failure<T, F>) this);
        }
    }

    /**
     * Calls the given runnable if this is a success attempt.
     *
     * @param runnable  The runnable.
     * @return This attempt.
     * @since 5.0
     */
    default Attempt<T, F> onSuccess(Runnable runnable) {
        if (this instanceof Success) {
            runnable.run();
        }
        return this;
    }

    /**
     * Calls the given consumer if this is a success attempt.
     *
     * @param consumer  The consumer with the value.
     * @return This attempt.
     * @since 5.0
     */
    default Attempt<T, F> onSuccess(Consumer<T> consumer) {
        if (this instanceof Success) {
            consumer.accept(get());
        }
        return this;
    }

    /**
     * Calls the given consumer if this is a failure attempt.
     *
     * @param runnable  The runnable.
     * @return This attempt.
     * @since 5.0
     */
    default Attempt<T, F> onFailure(Runnable runnable) {
        if (this instanceof Failure) {
            runnable.run();
        }
        return this;
    }

    /**
     * Calls the given consumer if this is a failure attempt.
     *
     * @param consumer  The consumer with the failure instance.
     * @return This attempt.
     * @since 5.0
     */
    default Attempt<T, F> onFailure(Consumer<Failure<T, F>> consumer) {
        if (this instanceof Failure) {
            consumer.accept((Failure<T, F>) this);
        }
        return this;
    }

    /**
     * Calls the given runnable if this is a failure attempt.
     *
     * @param consumer  The consumer with the failure reason.
     * @return This attempt.
     * @since 5.0
     */
    default Attempt<T, F> onFailureReason(Consumer<F> consumer) {
        if (this instanceof Failure) {
            consumer.accept(getFailureReason());
        }
        return this;
    }

    /**
     * Represents a successful attempt with a value.
     *
     * @param <T>   The type of the value.
     * @param <F>   The type of failure reason.
     * @since 5.0
     */
    final class Success<T, F extends FailureReason> implements Attempt<T, F> {
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
            throw new UnsupportedOperationException("No failure reason as attempt is a success");
        }

        @Override
        public Message getFailureMessage() {
            throw new UnsupportedOperationException("No failure message as attempt is a success");
        }

        @Override
        public String toString() {
            return "Success{"
                    + "value=" + value
                    + '}';
        }
    }

    /**
     * Represents a failed attempt with a reason.
     *
     * @param <T>   The type of the value.
     * @param <F>   The type of failure reason.
     * @since 5.0
     */
    final class Failure<T, F extends FailureReason> implements Attempt<T, F> {
        private final F failureReason;
        private final Message message;

        Failure(F failureReason, Message message) {
            this.failureReason = failureReason;
            this.message = message;
        }

        @Override
        public T get() {
            throw new UnsupportedOperationException("No value as attempt is a failure");
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
