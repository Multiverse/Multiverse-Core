package com.onarandombox.MultiverseCore.utils.result;

import com.onarandombox.MultiverseCore.utils.message.Message;
import com.onarandombox.MultiverseCore.utils.message.MessageReplacement;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public sealed interface Result<S extends SuccessReason, F extends FailureReason> permits Result.Success, Result.Failure {
    static <F extends FailureReason, S extends SuccessReason> Result<S, F> success(
            S successReason, MessageReplacement... replacements) {
        return new Success<>(successReason, replacements);
    }

    static <F extends FailureReason, S extends SuccessReason> Result<S, F> failure(
            F failureReason, MessageReplacement... replacements) {
        return new Failure<>(failureReason, replacements);
    }

    boolean isSuccess();

    boolean isFailure();

    S getSuccessReason();

    F getFailureReason();

    @NotNull Message getReasonMessage();

    default Result<S, F> onSuccess(Consumer<S> consumer) {
        if (this.isSuccess()) {
            consumer.accept(this.getSuccessReason());
        }
        return this;
    }

    default Result<S, F> onFailure(Consumer<F> consumer) {
        if (this.isFailure()) {
            consumer.accept(this.getFailureReason());
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

    final class Success<F extends FailureReason, S extends SuccessReason> implements Result<S, F> {
        private final S successReason;
        private final MessageReplacement[] replacements;

        public Success(S successReason, MessageReplacement[] replacements) {
            this.successReason = successReason;
            this.replacements = replacements;
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
            return Message.of(successReason, "Success!", replacements);
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
        private final MessageReplacement[] replacements;

        public Failure(F failureReason, MessageReplacement[] replacements) {
            this.failureReason = failureReason;
            this.replacements = replacements;
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
            throw new NoSuchElementException("No reason for success");
        }

        @Override
        public F getFailureReason() {
            return failureReason;
        }

        @Override
        public @NotNull Message getReasonMessage() {
            return Message.of(failureReason, "Success!", replacements);
        }

        @Override
        public String toString() {
            return "Failure{"
                    + "reason=" + failureReason
                    + '}';
        }
    }
}
