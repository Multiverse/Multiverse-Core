package com.onarandombox.MultiverseCore.utils.result;

import com.onarandombox.MultiverseCore.utils.message.Message;
import com.onarandombox.MultiverseCore.utils.message.MessageReplacement;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface Result<S extends SuccessReason, F extends FailureReason> permits Result.Success, Result.Failure {

    static <F extends FailureReason, S extends SuccessReason> Result<S, F> success(
            S successReason, MessageReplacement...replacements) {
        return new Success<>(successReason, replacements);
    }

    static <F extends FailureReason, S extends SuccessReason> Result<S, F> success(S successReason, Message message) {
        return new Success<>(successReason, message);
    }

    static <F extends FailureReason, S extends SuccessReason> Result<S, F> failure(
            F failureReason, MessageReplacement...replacements) {
        return new Failure<>(failureReason, replacements);
    }

    static <F extends FailureReason, S extends SuccessReason> Result<S, F> failure(F failureReason, Message message) {
        return new Failure<>(failureReason, message);
    }
    boolean isSuccess();

    boolean isFailure();

    S getSuccessReason();

    F getFailureReason();

    @NotNull Message getReasonMessage();

    default Result<S, F> onSuccess(Consumer<Success<F, S>> consumer) {
        if (this instanceof Success) {
            consumer.accept((Success<F, S>) this);
        }
        return this;
    }

    default Result<S, F> onFailure(Consumer<Failure<S, F>> consumer) {
        if (this instanceof Failure) {
            consumer.accept((Failure<S, F>) this);
        }
        return this;
    }

    default Result<S, F> onSuccessReason(S successReason, Consumer<S> consumer) {
        if (this.isSuccess() && this.getSuccessReason() == successReason) {
            consumer.accept(this.getSuccessReason());
        }
        return this;
    }

    default Result<S, F> onFailureReason(F failureReason, Consumer<F> consumer) {
        if (this.isFailure() && this.getFailureReason() == failureReason) {
            consumer.accept(this.getFailureReason());
        }
        return this;
    }

    default Result<S, F> onSuccessThen(Function<Success<F, S>, Result<S, F>> function) {
        if (this instanceof Success) {
            return function.apply((Success<F, S>) this);
        }
        return this;
    }

    default Result<S, F> onFailureThen(Function<Failure<S, F>, Result<S, F>> function) {
        if (this instanceof Failure) {
            return function.apply((Failure<S, F>) this);
        }
        return this;
    }

    default <R> R fold(Function<Failure<S, F>, R> failureFunc, Function<Success<F, S>, R> successFunc) {
        if (this instanceof Failure) {
            return failureFunc.apply((Failure<S, F>) this);
        } else if (this instanceof Success) {
            return successFunc.apply((Success<F, S>) this);
        }
        throw new IllegalStateException("Unknown result type: " + this.getClass().getName());
    }

    final class Success<F extends FailureReason, S extends SuccessReason> implements Result<S, F> {
        private final S successReason;
        private final Message message;

        public Success(S successReason, Message message) {
            this.successReason = successReason;
            this.message = message;
        }

        public Success(S successReason, MessageReplacement[] replacements) {
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
            throw new NoSuchElementException("No reason for failure");
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

    final class Failure<S extends SuccessReason, F extends FailureReason> implements Result<S, F> {
        private final F failureReason;
        private final Message message;

        public Failure(F failureReason, Message message) {
            this.failureReason = failureReason;
            this.message = message;
        }

        public Failure(F failureReason, MessageReplacement[] replacements) {
            this.failureReason = failureReason;
            this.message = Message.of(failureReason, "Success!", replacements);
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
            throw new NoSuchElementException("No reason for failure");
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
