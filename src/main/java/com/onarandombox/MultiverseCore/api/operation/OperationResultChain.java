package com.onarandombox.MultiverseCore.api.operation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A response that have multiple results.
 */
public class OperationResultChain implements OperationResult {

    /**
     * Create a new {@link OperationResultChain} instance.
     * Does not continue if one result is not successful.
     *
     * @return The new {@link OperationResultChain} instance.
     */
    public static OperationResultChain create() {
        return new OperationResultChain(false);
    }

    /**
     * Create a new {@link OperationResultChain} instance.
     *
     * @param continueIfFail If true, the response will continue to add results even if one of the results is not successful.
     * @return The new {@link OperationResultChain} instance.
     */
    public static OperationResultChain create(boolean continueIfFail) {
        return new OperationResultChain(continueIfFail);
    }

    /**
     * Create a response with a single result.
     * Does not continue if one result is not successful.
     *
     * @param result    The result.
     * @return The new {@link OperationResultChain} instance.
     */
    public static OperationResultChain of(OperationResult result) {
        return new OperationResultChain(false).addResult(result);
    }

    private final Set<OperationResult> results;
    private final boolean continueIfFail;

    private boolean isSuccessful = true;

    /**
     * @param continueIfFail If true, the response will continue to add results even if one of the results is not successful.
     */
    protected OperationResultChain(boolean continueIfFail) {
        this.results = new HashSet<>();
        this.continueIfFail = continueIfFail;
    }

    /**
     * Add a result to the response.
     *
     * @param resultSupplier    The supplier of the result.
     * @return self
     */
    public @NotNull OperationResultChain then(Supplier<OperationResult> resultSupplier) {
        if (!continueIfFail && !isSuccessful) {
            return this;
        }
        return addResult(resultSupplier.get());
    }

    /**
     * Add a result to the response.
     *
     * @param result    The result.
     * @return self
     */
    public @NotNull OperationResultChain addResult(OperationResult result) {
        if (!continueIfFail && !isSuccessful) {
            return this;
        }

        results.add(result);
        if (!result.asBoolean()) {
            isSuccessful = false;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return results.stream().map(OperationResult::getName).collect(Collectors.joining(", "));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean asBoolean() {
        return isSuccessful;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasResult(@Nullable OperationResult result) {
        return results.contains(result);
    }

    @Override
    public String toString() {
        return "ActionResponse{" +
                "results=" + getName() +
                "; continueIfFail=" + continueIfFail +
                "; isSuccessful=" + isSuccessful +
                '}';
    }
}
