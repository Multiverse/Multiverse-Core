package com.onarandombox.MultiverseCore.api.action;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

/**
 * A response that have multiple results.
 */
public class ActionResponse implements ActionResult {
    private final Set<ActionResult> results;
    private final boolean continueIfFail;

    private boolean isSuccessful = true;

    public ActionResponse() {
        this(false);
    }

    /**
     * @param continueIfFail If true, the response will continue to add results even if one of the results is not successful.
     */
    public ActionResponse(boolean continueIfFail) {
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
        if (!result.isSuccessful()) {
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
    public boolean hasResult(ActionResult result) {
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
