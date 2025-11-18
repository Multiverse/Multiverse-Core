package org.mvplugins.multiverse.core.utils.result;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.vavr.control.Either;
import io.vavr.control.Try;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.exceptions.MultiverseException;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;

/**
 * Represents an attempt to process a value that can fail with a reason that has a localized message.
 *
 * @param <T>   The type of the value.
 * @param <F>   The type of failure reason.
 */
public sealed interface Attempt<T, F extends FailureReason> permits Attempt.Success, Attempt.Failure {

    /**
     * Creates a new success attempt.
     *
     * @param value The value.
     * @param <T>   The type of the value.
     * @param <F>   The type of failure reason.
     * @return The new success attempt.
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
     */
    static <T, F extends FailureReason> Attempt<T, F> failure(F failureReason, Message message) {
        return new Failure<>(failureReason, message);
    }

    /**
     * Gets the value of this attempt. Exceptions will be thrown if this is a failure attempt.
     *
     * @return The value.
     */
    T get();

    /**
     * Gets the value of this attempt or null if this is a failure attempt.
     *
     * @return The value or null.
     */
    T getOrNull();

    /**
     * Gets the value of this attempt or a default value if this is a failure attempt.
     *
     * @param defaultValue The default value.
     * @return The value or the default value.
     */
    T getOrElse(T defaultValue);

    /**
     * Gets the value of this attempt or throws an exception if this is a failure attempt.
     *
     * @param exceptionSupplier The exception supplier.
     * @return The value.
     */
    <X extends Throwable> T getOrThrow(Function<Failure<T, F>, X> exceptionSupplier) throws X;

    /**
     * Gets the reason for failure. Exceptions will be thrown if this is a success attempt.
     *
     * @return The reason for failure.
     */
    F getFailureReason();

    /**
     * Gets the message for failure. Exceptions will be thrown if this is a success attempt.
     *
     * @return The message for failure.
     */
    Message getFailureMessage();

    /**
     * Returns whether this attempt is a success.
     *
     * @return Whether this attempt is a success.
     */
    boolean isSuccess();

    /**
     * Returns whether this attempt is a failure.
     *
     * @return Whether this attempt is a failure.
     */
    boolean isFailure();

    /**
     * Converts this {@link Attempt} instance to an equivalent {@link Try} representation. Defaults to a
     * {@link MultiverseException} with failure message if this is a failure attempt.
     *
     * @return A {@link Try} instance representing the result of this {@code Attempt}.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    Try<T> toTry();

    /**
     * Converts this attempt to a {@code Try} instance. If this attempt represents a failure, the
     * provided exception supplier will be invoked to create a failed try.
     *
     * @param throwableFunction A function that provides a throwable in case of failure.
     * @return The {@link Try} instance corresponding to this attempt.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    Try<T> toTry(Function<Failure<T, F>, Throwable> throwableFunction);

    /**
     * Runs the given runnable after this attempt regardless of success or failure.
     *
     * @param runnable  The runnable.
     * @return This attempt.
     */
    default Attempt<T, F> thenRun(Runnable runnable) {
        runnable.run();
        return this;
    }

    /**
     * Accepts either the value or the failure reason depending on the result type.
     * This will run regardless of success or failure.
     *
     * @param consumer The consumer with either the value or the failure reason.
     * @return This attempt.
     */
    Attempt<T, F> thenAccept(Consumer<Either<T, F>> consumer);

    /**
     * Peeks at the value if this is a success attempt.
     *
     * @param consumer The consumer with the value.
     * @return This attempt.
     */
     Attempt<T, F> peek(Consumer<T> consumer);

    /**
     * Maps the value to another value if this is a success attempt.
     *
     * @param mapper    The mapper.
     * @param <U>       The type of the new value.
     * @return The new attempt.
     */
    <U> Attempt<U, F> map(Function<? super T, ? extends U> mapper);

    /**
     * Maps the value to another attempt if this is a success attempt.
     *
     * @param mapper    The mapper.
     * @param <U>       The type of the new value.
     * @return The new attempt.
     */
    <U> Attempt<U, F> map(Supplier<? extends U> mapper);

    /**
     * Maps the value to another attempt with the same fail reason if this is a success attempt.
     *
     * @param mapper    The mapper.
     * @param <U>       The type of the new value.
     * @return The new attempt.
     */
    <U> Attempt<U, F> mapAttempt(Function<? super T, Attempt<U, F>> mapper);

    /**
     * Maps the value to another attempt with the same fail reason if this is a success attempt.
     *
     * @param mapper    The mapper.
     * @param <U>       The type of the new value.
     * @return The new attempt.
     */
    <U> Attempt<U, F> mapAttempt(Supplier<Attempt<U, F>> mapper);

    /**
     * Maps to another attempt with a different fail reason.
     *
     * @param failureReason The new fail reason.
     * @param <UF>          The type of the new fail reason.
     * @return The new attempt.
     */
    <UF extends FailureReason> Attempt<T, UF> transform(UF failureReason);

    /**
     * Maps attempt result to another value.
     *
     * @param successMapper Action taken if the attempt is a success
     * @param failureMapper Action taken if the attempt is a failure
     * @param <U> The transformed value type
     * @return The transformed value
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    <U> U transform(Function<T, U> successMapper, Function<F, U> failureMapper);

    /**
     * Calls either the failure or success function depending on the result type.
     *
     * @param failureMapper The failure function.
     * @param successMapper The success function.
     * @param <N>           The type of the new value.
     * @return The result of the function.
     */
    <N> N fold(Function<Failure<T, F>, N> failureMapper, Function<T, N> successMapper);

    /**
     * Calls the given runnable if this is a success attempt.
     *
     * @param runnable  The runnable.
     * @return This attempt.
     */
    Attempt<T, F> onSuccess(Runnable runnable);

    /**
     * Calls the given consumer if this is a success attempt.
     *
     * @param consumer  The consumer with the value.
     * @return This attempt.
     */
    Attempt<T, F> onSuccess(Consumer<T> consumer);

    /**
     * Calls the given consumer if this is a failure attempt.
     *
     * @param runnable  The runnable.
     * @return This attempt.
     */
    Attempt<T, F> onFailure(Runnable runnable);

    /**
     * Calls the given consumer if this is a failure attempt.
     *
     * @param consumer  The consumer with the failure instance.
     * @return This attempt.
     */
    Attempt<T, F> onFailure(Consumer<Failure<T, F>> consumer);

    /**
     * Calls the given runnable if this is a failure attempt.
     *
     * @param consumer  The consumer with the failure reason.
     * @return This attempt.
     */
    Attempt<T, F> onFailureReason(Consumer<F> consumer);

    /**
     * Represents a successful attempt with a value.
     *
     * @param <T>   The type of the value.
     * @param <F>   The type of failure reason.
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
        public T getOrNull() {
            return value;
        }

        @Override
        public T getOrElse(T defaultValue) {
            return value;
        }

        @Override
        public <X extends Throwable> T getOrThrow(Function<Failure<T, F>, X> exceptionSupplier) throws X  {
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
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public Try<T> toTry() {
            return Try.success(value);
        }

        @Override
        public Try<T> toTry(Function<Failure<T, F>, Throwable> throwableFunction) {
            return Try.success(value);
        }

        @Override
        public Attempt<T, F> thenAccept(Consumer<Either<T, F>> consumer) {
            consumer.accept(Either.left(value));
            return this;
        }

        @Override
        public Attempt<T, F> peek(Consumer<T> consumer) {
            consumer.accept(value);
            return this;
        }

        @Override
        public <U> Attempt<U, F> map(Function<? super T, ? extends U> mapper) {
            return new Success<>(mapper.apply(value));
        }

        @Override
        public <U> Attempt<U, F> map(Supplier<? extends U> mapper) {
            return new Success<>(mapper.get());
        }

        @Override
        public <U> Attempt<U, F> mapAttempt(Function<? super T, Attempt<U, F>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public <U> Attempt<U, F> mapAttempt(Supplier<Attempt<U, F>> mapper) {
            return mapper.get();
        }

        @Override
        public <UF extends FailureReason> Attempt<T, UF> transform(UF failureReason) {
            return changeFailureType();
        }

        @Override
        public <U> U transform(Function<T, U> successMapper, Function<F, U> failureMapper) {
            return successMapper.apply(value);
        }

        @Override
        public <N> N fold(Function<Failure<T, F>, N> failureMapper, Function<T, N> successMapper) {
            return successMapper.apply(value);
        }

        @Override
        public Attempt<T, F> onSuccess(Runnable runnable) {
            runnable.run();
            return this;
        }

        @Override
        public Attempt<T, F> onSuccess(Consumer<T> consumer) {
            consumer.accept(value);
            return this;
        }

        @Override
        public Attempt<T, F> onFailure(Runnable runnable) {
            return this;
        }

        @Override
        public Attempt<T, F> onFailure(Consumer<Failure<T, F>> consumer) {
            return this;
        }

        @Override
        public Attempt<T, F> onFailureReason(Consumer<F> consumer) {
            return this;
        }

        private <UF extends FailureReason> Attempt<T, UF> changeFailureType() {
            @SuppressWarnings("unchecked")
            Attempt<T, UF> mappedSuccess = (Attempt<T, UF>) this;
            return mappedSuccess;
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
     */
    final class Failure<T, F extends FailureReason> implements Attempt<T, F> {
        private final F failureReason;
        private final Message message;
        private final Failure<?, ?> causeBy;

        Failure(F failureReason, Message message) {
            this(failureReason, message, null);
        }

        Failure(Failure<?, F> failure) {
            this(failure.failureReason, failure.message, failure.causeBy);
        }

        Failure(F failureReason, Message message, Failure<?, ?> causeBy) {
            this.failureReason = failureReason;
            this.message = message;
            this.causeBy = causeBy;
        }

        @Override
        public T get() {
            throw new UnsupportedOperationException("No value as attempt is a failure");
        }

        @Override
        public T getOrNull() {
            return null;
        }

        @Override
        public T getOrElse(T defaultValue) {
            return defaultValue;
        }

        @Override
        public <X extends Throwable> T getOrThrow(Function<Failure<T, F>, X> exceptionSupplier) throws X {
            throw exceptionSupplier.apply(this);
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
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public Try<T> toTry() {
            return Try.failure(new MultiverseException(message));
        }

        @Override
        public Try<T> toTry(Function<Failure<T, F>, Throwable> throwableFunction) {
            return Try.failure(throwableFunction.apply(this));
        }

        @Override
        public Attempt<T, F> thenAccept(Consumer<Either<T, F>> consumer) {
            consumer.accept(Either.right(failureReason));
            return this;
        }

        @Override
        public Attempt<T, F> peek(Consumer<T> consumer) {
            return this;
        }

        @Override
        public <U> Attempt<U, F> map(Function<? super T, ? extends U> mapper) {
            return changeValueType();
        }

        @Override
        public <U> Attempt<U, F> map(Supplier<? extends U> mapper) {
            return changeValueType();
        }

        @Override
        public <U> Attempt<U, F> mapAttempt(Function<? super T, Attempt<U, F>> mapper) {
            return changeValueType();
        }

        @Override
        public <U> Attempt<U, F> mapAttempt(Supplier<Attempt<U, F>> mapper) {
            return changeValueType();
        }

        @Override
        public <UF extends FailureReason> Attempt<T, UF> transform(UF failureReason) {
            return new Failure<>(failureReason, getFailureMessage(), this);
        }

        @Override
        public <U> U transform(Function<T, U> successMapper, Function<F, U> failureMapper) {
            return failureMapper.apply(failureReason);
        }

        @Override
        public <N> N fold(Function<Failure<T, F>, N> failureMapper, Function<T, N> successMapper) {
            return failureMapper.apply(this);
        }

        @Override
        public Attempt<T, F> onSuccess(Runnable runnable) {
            return this;
        }

        @Override
        public Attempt<T, F> onSuccess(Consumer<T> consumer) {
            return this;
        }

        @Override
        public Attempt<T, F> onFailure(Runnable runnable) {
            runnable.run();
            return this;
        }

        @Override
        public Attempt<T, F> onFailure(Consumer<Failure<T, F>> consumer) {
            consumer.accept(this);
            return this;
        }

        @Override
        public Attempt<T, F> onFailureReason(Consumer<F> consumer) {
            consumer.accept(failureReason);
            return this;
        }

        private <U> Attempt<U, F> changeValueType() {
            @SuppressWarnings("unchecked")
            Attempt<U, F> mappedFailure = (Attempt<U, F>) this;
            return mappedFailure;
        }

        @Override
        public String toString() {
            return "Failure{"
                    + "reason=" + failureReason
                    + (causeBy != null ? ", causeBy=" + causeBy : "")
                    + '}';
        }
    }
}
