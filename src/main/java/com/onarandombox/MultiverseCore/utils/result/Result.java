package com.onarandombox.MultiverseCore.utils.result;

import java.util.function.Consumer;

public interface Result<S extends SuccessReason, F extends FailureReason> {

    static <F extends FailureReason, S extends SuccessReason> Result<S, F> success(S successReason) {
        return new Success<>(successReason);
    }

    static <F extends FailureReason, S extends SuccessReason> Result<S, F> failure(F failureReason) {
        return new Failure<>(failureReason);
    }

    boolean isSuccess();

    boolean isFailure();

    S successReason();

    F failureReason();

    default Result<S, F> success(Consumer<SuccessReason> consumer) {
        if (this.isSuccess()) {
            consumer.accept(this.successReason());
        }
        return this;
    }

    default Result<S, F> failure(Consumer<FailureReason> consumer) {
        if (this.isFailure()) {
            consumer.accept(this.failureReason());
        }
        return this;
    }

    default Result<S, F> successWithReason(S successReason, Consumer<SuccessReason> consumer) {
        if (this.isSuccess() && this.successReason() == successReason) {
            consumer.accept(this.successReason());
        }
        return this;
    }

    default Result<S, F> failureWithReason(F failureReason, Consumer<FailureReason> consumer) {
        if (this.isFailure() && this.failureReason() == failureReason) {
            consumer.accept(this.failureReason());
        }
        return this;
    }

    record Success<F extends FailureReason, S extends SuccessReason>(S successReason) implements Result<S, F> {
        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public F failureReason() {
            return null;
        }
    }

    record Failure<S extends SuccessReason, F extends FailureReason>(F failureReason) implements Result<S, F> {
        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public S successReason() {
            return null;
        }
    }
}
