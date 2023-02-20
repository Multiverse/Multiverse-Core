package com.onarandombox.MultiverseCore.api.action;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A response that have multiple results.
 */
public class ActionResponse implements ActionResult {

    /**
     * Create a new {@link ActionResponse} instance.
     * Does not continue if one result is not successful.
     *
     * @return The new {@link ActionResponse} instance.
     */
    public static ActionResponse create() {
        return new ActionResponse(false);
    }

    /**
     * Create a new {@link ActionResponse} instance.
     *
     * @param continueIfFail If true, the response will continue to add results even if one of the results is not successful.
     * @return The new {@link ActionResponse} instance.
     */
    public static ActionResponse create(boolean continueIfFail) {
        return new ActionResponse(continueIfFail);
    }

    /**
     * Create a response with a single result.
     * Does not continue if one result is not successful.
     *
     * @param result    The result.
     * @return The new {@link ActionResponse} instance.
     */
    public static ActionResponse of(ActionResult result) {
        return new ActionResponse(false).addResult(result);
    }

    private final Set<ActionResult> results;
    private final boolean continueIfFail;

    private boolean isSuccessful = true;

    /**
     * @param continueIfFail If true, the response will continue to add results even if one of the results is not successful.
     */
    protected ActionResponse(boolean continueIfFail) {
        this.results = new HashSet<>();
        this.continueIfFail = continueIfFail;
    }

    /**
     * Add a result to the response.
     *
     * @param resultSupplier    The supplier of the result.
     * @return self
     */
    public @NotNull ActionResponse then(Supplier<ActionResult> resultSupplier) {
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
    public @NotNull ActionResponse addResult(ActionResult result) {
        if (!continueIfFail && !isSuccessful) {
            return this;
        }

        results.add(result);
        if (result.isUnsuccessful()) {
            isSuccessful = false;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return results.stream().map(ActionResult::getName).collect(Collectors.joining(", "));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSuccessful() {
        return isSuccessful;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnsuccessful() {
        return !isSuccessful;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasResult(@Nullable ActionResult result) {
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
