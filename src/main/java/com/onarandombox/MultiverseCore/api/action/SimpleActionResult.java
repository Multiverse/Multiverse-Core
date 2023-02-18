package com.onarandombox.MultiverseCore.api.action;

import org.jetbrains.annotations.Nullable;

public class SimpleActionResult implements ActionResult {
    private final String name;
    private final boolean isSuccessful;

    /**
     * Creates a new ActionResult.
     *
     * @param name          The name of the result.
     * @param isSuccessful  true if the result is successful.
     */
    public SimpleActionResult(String name, boolean isSuccessful) {
        this.name = name;
        this.isSuccessful = isSuccessful;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
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
    public boolean hasResult(@Nullable ActionResult result) {
        return result == this;
    }
}
