package com.onarandombox.MultiverseCore.utils.result;

import io.vavr.control.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ResultGroup {

    public static Builder builder() {
        return new Builder(true);
    }

    public static Builder builder(boolean stopOnFailure) {
        return new Builder(stopOnFailure);
    }

    private final boolean isSuccess;
    private final List<Result<?, ?>> results;

    ResultGroup(boolean isSuccess, List<Result<?, ?>> results) {
        this.isSuccess = isSuccess;
        this.results = results;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    public ResultGroup onSuccess(Runnable successRunnable) {
        if (isSuccess) {
            successRunnable.run();
        }
        return this;
    }

    public ResultGroup onFailure(Runnable failureRunnable) {
        if (isFailure()) {
            failureRunnable.run();
        }
        return this;
    }

    public <S extends SuccessReason> ResultGroup onSuccessReason(Class<S> successReasonClass, Consumer<S> successConsumer) {
        getSuccessReason(successReasonClass).peek(successConsumer);
        return this;
    }

    public <F extends FailureReason> ResultGroup onFailureReason(Class<F> failureReasonClass, Consumer<F> failureConsumer) {
        getFailureReason(failureReasonClass).peek(failureConsumer);
        return this;
    }

    public <S extends SuccessReason> ResultGroup onSuccessReason(S successReason, Runnable successRunnable) {
        getSuccessReason(successReason.getClass()).filter(successReason::equals).peek(reason -> successRunnable.run());
        return this;
    }

    public <S extends SuccessReason> Option<S> getSuccessReason(Class<S> successReasonClass) {
        if (isFailure()) {
            return Option.none();
        }
        return Option.ofOptional(results.stream()
                .map(Result::getSuccessReason)
                .filter(successReasonClass::isInstance)
                .map(successReasonClass::cast)
                .findFirst());
    }

    public <F extends FailureReason> Option<F> getFailureReason(Class<F> failureReasonClass) {
        if (isSuccess()) {
            return Option.none();
        }
        return Option.ofOptional(results.stream()
                .map(Result::getFailureReason)
                .filter(failureReasonClass::isInstance)
                .map(failureReasonClass::cast)
                .findFirst());
    }

    @Override
    public String toString() {
        return "ResultGroup{" +
                "isSuccess=" + isSuccess +
                ", results={" + results.stream().map(Objects::toString).collect(Collectors.joining(", ")) + "}" +
                '}';
    }

    public static class Builder {
        private final boolean stopOnFailure;
        private final List<Result<?, ?>> results;

        private boolean isSuccess = true;

        public Builder(boolean stopOnFailure) {
            this.stopOnFailure = stopOnFailure;
            this.results = new ArrayList<>();
        }

        public Builder then(Supplier<Result<?, ?>> resultSupplier) {
            if (!isSuccess && stopOnFailure) {
                return this;
            }
            Result<?, ?> result = resultSupplier.get();
            if (result.isFailure()) {
                isSuccess = false;
            }
            results.add(result);
            return this;
        }

        public ResultGroup build() {
            return new ResultGroup(isSuccess, results);
        }
    }
}
