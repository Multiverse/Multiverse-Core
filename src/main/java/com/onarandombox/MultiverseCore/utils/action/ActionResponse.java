package com.onarandombox.MultiverseCore.utils.action;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ActionResponse implements ActionResult {
    private final Set<ActionResult> results;
    private final boolean continueIfFail;

    private boolean isSuccessful = true;

    public ActionResponse() {
        this(false);
    }

    public ActionResponse(boolean continueIfFail) {
        this.results = new HashSet<>();
        this.continueIfFail = continueIfFail;
    }

    public ActionResponse then(Supplier<ActionResult> resultSupplier) {
        if (!continueIfFail && !isSuccessful) {
            return this;
        }
        return addResult(resultSupplier.get());
    }

    public ActionResponse addResult(ActionResult result) {
        if (!continueIfFail && !isSuccessful) {
            return this;
        }

        results.add(result);
        if (!result.isSuccessful()) {
            isSuccessful = false;
        }
        return this;
    }

    @Override
    public String getName() {
        return results.stream().map(ActionResult::getName).collect(Collectors.joining(", "));
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

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
