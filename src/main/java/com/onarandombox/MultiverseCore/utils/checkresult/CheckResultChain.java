package com.onarandombox.MultiverseCore.utils.checkresult;

import com.google.common.collect.Iterables;
import com.onarandombox.MultiverseCore.utils.message.Message;
import io.vavr.control.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CheckResultChain {
    public static Builder builder() {
        return new Builder(true);
    }

    public static Builder builder(boolean stopOnFailure) {
        return new Builder(stopOnFailure);
    }

    private final boolean isSuccess;
    private final List<CheckResult<?, ?>> results;

    CheckResultChain(boolean isSuccess, List<CheckResult<?, ?>> results) {
        this.isSuccess = isSuccess;
        this.results = results;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    public CheckResultChain onSuccess(Runnable successRunnable) {
        if (isSuccess) {
            successRunnable.run();
        }
        return this;
    }

    public CheckResultChain onFailure(Runnable failureRunnable) {
        if (isFailure()) {
            failureRunnable.run();
        }
        return this;
    }

    public CheckResultChain onSuccess(Consumer<CheckResultChain> successRunnable) {
        if (isSuccess) {
            successRunnable.accept(this);
        }
        return this;
    }

    public CheckResultChain onFailure(Consumer<CheckResultChain> failureRunnable) {
        if (isFailure()) {
            failureRunnable.accept(this);
        }
        return this;
    }

    public <S extends SuccessReason> CheckResultChain onSuccessReason(Class<S> successReasonClass, Consumer<S> successConsumer) {
        getSuccessReason(successReasonClass).peek(successConsumer);
        return this;
    }

    public <F extends FailureReason> CheckResultChain onFailureReason(Class<F> failureReasonClass, Consumer<F> failureConsumer) {
        getFailureReason(failureReasonClass).peek(failureConsumer);
        return this;
    }

    public <S extends SuccessReason> CheckResultChain onSuccessReason(S successReason, Runnable successRunnable) {
        getSuccessReason(successReason.getClass()).filter(successReason::equals).peek(reason -> successRunnable.run());
        return this;
    }

    public <S extends SuccessReason> Option<S> getSuccessReason(Class<S> successReasonClass) {
        if (isFailure()) {
            return Option.none();
        }
        return Option.ofOptional(results.stream()
                .map(CheckResult::getSuccessReason)
                .filter(successReasonClass::isInstance)
                .map(successReasonClass::cast)
                .findFirst());
    }

    public <F extends FailureReason> Option<F> getFailureReason(Class<F> failureReasonClass) {
        if (isSuccess()) {
            return Option.none();
        }
        return Option.ofOptional(results.stream()
                .map(CheckResult::getFailureReason)
                .filter(failureReasonClass::isInstance)
                .map(failureReasonClass::cast)
                .findFirst());
    }

    public Message getLastResultMessage() {
        return Iterables.getLast(results).getReasonMessage();
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
        private final List<CheckResult<?, ?>> results;

        private boolean isSuccess = true;

        public Builder(boolean stopOnFailure) {
            this.stopOnFailure = stopOnFailure;
            this.results = new ArrayList<>();
        }

        public Builder then(Supplier<CheckResult<?, ?>> resultSupplier) {
            if (!isSuccess && stopOnFailure) {
                return this;
            }
            CheckResult<?, ?> result = resultSupplier.get();
            if (result.isFailure()) {
                isSuccess = false;
            }
            results.add(result);
            return this;
        }

        public CheckResultChain build() {
            return new CheckResultChain(isSuccess, results);
        }
    }
}
