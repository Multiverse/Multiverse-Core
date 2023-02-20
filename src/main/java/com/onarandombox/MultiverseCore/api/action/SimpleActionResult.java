package com.onarandombox.MultiverseCore.api.action;

import org.jetbrains.annotations.Nullable;

/**
 * A simple implementation of {@link ActionResult}.
 */
public class SimpleActionResult implements ActionResult {

    /**
     * Creates a new ActionResult that is successful with the given name.
     *
     * @param name  The name of the result.
     * @return The new ActionResult.
     */
    public static ActionResult forSuccess(String name) {
        return new SimpleActionResult(name, true);
    }

    /**
     * Creates a new ActionResult that is unsuccessful with the given name.
     *
     * @param name  The name of the result.
     * @return The new ActionResult.
     */
    public static ActionResult forFailure(String name) {
        return new SimpleActionResult(name, false);
    }

    /**
     * Creates a new ActionResult.
     *
     * @param name          The name of the result.
     * @param isSuccessful  true if the result is successful.
     * @return The new ActionResult.
     */
    public static ActionResult of(String name, boolean isSuccessful) {
        return new SimpleActionResult(name, isSuccessful);
    }

    private final String name;
    private final boolean isSuccessful;

    /**
     * Creates a new ActionResult.
     *
     * @param name          The name of the result.
     * @param isSuccessful  true if the result is successful.
     */
    protected SimpleActionResult(String name, boolean isSuccessful) {
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
    public boolean isUnsuccessful() {
        return !isSuccessful;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasResult(@Nullable ActionResult result) {
        return result == this;
    }

    @Override
    public String toString() {
        return "SimpleActionResult{" +
                "name='" + name + '\'' +
                ", isSuccessful=" + isSuccessful +
                '}';
    }
}
