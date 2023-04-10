package com.onarandombox.MultiverseCore.utils.result;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

public class Result<S extends SuccessReason, F extends FailureReason> {

    public static <S extends SuccessReason, F extends FailureReason> Result<S, F> success(S successReason) {
        return new Result<S, F>(successReason);
    }

    public static <S extends SuccessReason, F extends FailureReason> Result<S, F> failure(F failureReason) {
        return new Result<S, F>(failureReason);
    }

    private S successReason = null;
    private F failureReason = null;
    private final boolean success;

    protected Result(S successReason) {
        this.successReason = successReason;
        this.success = true;
    }

    protected Result(F failureReason) {
        this.failureReason = failureReason;
        this.success = false;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean isFailure() {
        return !this.success;
    }

    public @Nullable S getSuccessReason() {
        return successReason;
    }

    public @Nullable F getFailureReason() {
        return failureReason;
    }

    public Result<S, F> success(Consumer<SuccessReason> consumer) {
        if (this.isSuccess()) {
            consumer.accept(this.getSuccessReason());
        }
        return this;
    }

    public Result<S, F> failure(Consumer<FailureReason> consumer) {
        if (this.isFailure()) {
            consumer.accept(this.getFailureReason());
        }
        return this;
    }

    @Override
    public String toString() {
        return isSuccess() ? "Success:" + successReason : "Failure:" + failureReason;
    }
}
