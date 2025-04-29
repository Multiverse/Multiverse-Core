package org.mvplugins.multiverse.core.utils.result;

import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;

/**
 * Represents result of an operation with a reason for success or failure that has localized messages.
 *
 * @param <S>   The type of success reason.
 * @param <F>   The type of failure reason.
 */
public sealed interface Result<S extends SuccessReason, F extends FailureReason>
        permits Result.Success, Result.Failure {

    /**
     * Creates a new success result.
     *
     * @param successReason The reason for success.
     * @param replacements  The replacements for the success message.
     * @param <F>           The type of failure reason.
     * @param <S>           The type of success reason.
     * @return The new success result.
     */
    static <F extends FailureReason, S extends SuccessReason> Result<S, F> success(
            S successReason, MessageReplacement... replacements) {
        return new Success<>(successReason, replacements);
    }

    /**
     * Creates a new success result.
     *
     * @param successReason The reason for success.
     * @param message       The custom message for success. This will override the default message.
     * @param <F>           The type of failure reason.
     * @param <S>           The type of success reason.
     * @return The new success result.
     */
    static <F extends FailureReason, S extends SuccessReason> Result<S, F> success(S successReason, Message message) {
        return new Success<>(successReason, message);
    }

    /**
     * Creates a new failure result.
     *
     * @param failureReason The reason for failure.
     * @param replacements  The replacements for the failure message.
     * @param <F>           The type of failure reason.
     * @param <S>           The type of success reason.
     * @return The new failure result.
     */
    static <F extends FailureReason, S extends SuccessReason> Result<S, F> failure(
            F failureReason, MessageReplacement... replacements) {
        return new Failure<>(failureReason, replacements);
    }

    /**
     * Creates a new failure result.
     *
     * @param failureReason The reason for failure.
     * @param message       The custom message for failure. This will override the default message.
     * @param <F>           The type of failure reason.
     * @param <S>           The type of success reason.
     * @return The new failure result.
     */
    static <F extends FailureReason, S extends SuccessReason> Result<S, F> failure(F failureReason, Message message) {
        return new Failure<>(failureReason, message);
    }

    /**
     * Returns whether this result is a success.
     *
     * @return Whether this result is a success.
     */
    boolean isSuccess();

    /**
     * Returns whether this result is a failure.
     *
     * @return Whether this result is a failure.
     */
    boolean isFailure();

    /**
     * Returns the reason for success.
     *
     * @return The reason for success.
     */
    S getSuccessReason();

    /**
     * Returns the reason for failure.
     *
     * @return The reason for failure.
     */
    F getFailureReason();

    /**
     * Returns the message for the reason of this result.
     *
     * @return The message for the reason.
     */
    @NotNull Message getReasonMessage();

    /**
     * Executes the given consumer if this result is a success.
     *
     * @param consumer  The consumer with success object.
     * @return This result.
     */
    default Result<S, F> onSuccess(Consumer<Success<S, F>> consumer) {
        if (this instanceof Success) {
            consumer.accept((Success<S, F>) this);
        }
        return this;
    }

    /**
     * Executes the given consumer if this result is a failure.
     *
     * @param consumer  The consumer with failure object.
     * @return This result.
     */
    default Result<S, F> onFailure(Consumer<Failure<S, F>> consumer) {
        if (this instanceof Failure) {
            consumer.accept((Failure<S, F>) this);
        }
        return this;
    }

    /**
     * Executes the given consumer if this result is a success and the success reason matches the given reason.
     *
     * @param successReason The success reason to match.
     * @param consumer      The consumer with success reason.
     * @return This result.
     */
    default Result<S, F> onSuccessReason(S successReason, Consumer<S> consumer) {
        if (this.isSuccess() && this.getSuccessReason() == successReason) {
            consumer.accept(this.getSuccessReason());
        }
        return this;
    }

    /**
     * Executes the given consumer if this result is a failure and the failure reason matches the given reason.
     *
     * @param failureReason The failure reason to match.
     * @param consumer      The consumer with failure reason.
     * @return This result.
     */
    default Result<S, F> onFailureReason(F failureReason, Consumer<F> consumer) {
        if (this.isFailure() && this.getFailureReason() == failureReason) {
            consumer.accept(this.getFailureReason());
        }
        return this;
    }

    /**
     * Executes the given function if this result is a success and returns the result of the function.
     *
     * @param function  The function with success object.
     * @return The result of the function.
     */
    default Result<S, F> onSuccessThen(Function<Success<S, F>, Result<S, F>> function) {
        if (this instanceof Success) {
            return function.apply((Success<S, F>) this);
        }
        return this;
    }

    /**
     * Executes the given function if this result is a failure and returns the result of the function.
     *
     * @param function  The function with failure object.
     * @return The result of the function.
     */
    default Result<S, F> onFailureThen(Function<Failure<S, F>, Result<S, F>> function) {
        if (this instanceof Failure) {
            return function.apply((Failure<S, F>) this);
        }
        return this;
    }

    /**
     * Executes either the failure or success function depending on the result type.
     *
     * @param failureFunc   The function with success reason.
     * @param successFunc   The function with success reason.
     * @param <R>           The type of the result.
     * @return The result of the function.
     */
    default <R> R fold(Function<Failure<S, F>, R> failureFunc, Function<Success<S, F>, R> successFunc) {
        if (this instanceof Failure) {
            return failureFunc.apply((Failure<S, F>) this);
        } else if (this instanceof Success) {
            return successFunc.apply((Success<S, F>) this);
        }
        throw new IllegalStateException("Unknown result type: " + this.getClass().getName());
    }

    /**
     * The class for a successful result.
     *
     * @param <F>   The type of failure reason.
     * @param <S>   The type of success reason.
     */
    final class Success<S extends SuccessReason, F extends FailureReason> implements Result<S, F> {
        private final S successReason;
        private final Message message;

        Success(S successReason, Message message) {
            this.successReason = successReason;
            this.message = message;
        }

        Success(S successReason, MessageReplacement[] replacements) {
            this.successReason = successReason;
            this.message = Message.of(successReason, "Success!", replacements);
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
        public S getSuccessReason() {
            return successReason;
        }

        @Override
        public F getFailureReason() {
            throw new UnsupportedOperationException("No reason for success");
        }

        @Override
        public @NotNull Message getReasonMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Success{"
                    + "reason=" + successReason
                    + '}';
        }
    }

    /**
     * The class for a failed result.
     *
     * @param <S>   The type of success reason.
     * @param <F>   The type of failure reason.
     */
    final class Failure<S extends SuccessReason, F extends FailureReason> implements Result<S, F> {
        private final F failureReason;
        private final Message message;

        Failure(F failureReason, Message message) {
            this.failureReason = failureReason;
            this.message = message;
        }

        Failure(F failureReason, MessageReplacement[] replacements) {
            this.failureReason = failureReason;
            this.message = Message.of(failureReason, "Failed!", replacements);
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
        public S getSuccessReason() {
            throw new UnsupportedOperationException("No reason for failure");
        }

        @Override
        public F getFailureReason() {
            return failureReason;
        }

        @Override
        public @NotNull Message getReasonMessage() {
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
