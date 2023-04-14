package com.onarandombox.MultiverseCore.utils.result;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public sealed interface Result<S extends SuccessReason, F extends FailureReason> permits Result.Success, Result.Failure {
    static <F extends FailureReason, S extends SuccessReason> Result<S, F> success(S successReason) {
        return new Success<>(successReason);
    }

    static <F extends FailureReason, S extends SuccessReason> Result<S, F> failure(F failureReason) {
        return new Failure<>(failureReason);
    }

    boolean isSuccess();

    boolean isFailure();

    S getSuccessReason();

    F getFailureReason();

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

    record Success<F extends FailureReason, S extends SuccessReason>(S getSuccessReason) implements Result<S, F> {
        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public F getFailureReason() {
            throw new NoSuchElementException("No failure reason for success");
        }
    }

    record Failure<S extends SuccessReason, F extends FailureReason>(F getFailureReason) implements Result<S, F> {
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
            throw new NoSuchElementException("No success reason for failure");
        }
    }
}
