package com.onarandombox.MultiverseCore.api.operation;

import org.jetbrains.annotations.Nullable;

/**
 * A simple implementation of {@link OperationResult}.
 */
public class SimpleOperationResult implements OperationResult {

    /**
     * Creates a new ActionResult that is successful with the given name.
     *
     * @param name  The name of the result.
     * @return The new ActionResult.
     */
    public static OperationResult forSuccess(String name) {
        return new SimpleOperationResult(name, true);
    }

    /**
     * Creates a new ActionResult that is unsuccessful with the given name.
     *
     * @param name  The name of the result.
     * @return The new ActionResult.
     */
    public static OperationResult forFailure(String name) {
        return new SimpleOperationResult(name, false);
    }

    /**
     * Creates a new ActionResult.
     *
     * @param name          The name of the result.
     * @param isSuccessful  true if the result is successful.
     * @return The new ActionResult.
     */
    public static OperationResult of(String name, boolean isSuccessful) {
        return new SimpleOperationResult(name, isSuccessful);
    }

    private final String name;
    private final boolean booleanState;

    /**
     * Creates a new ActionResult.
     *
     * @param name          The name of the result.
     * @param isSuccessful  true if the result is successful.
     */
    protected SimpleOperationResult(String name, boolean isSuccessful) {
        this.name = name;
        this.booleanState = isSuccessful;
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
    public boolean asBoolean() {
        return booleanState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasResult(@Nullable OperationResult result) {
        return result == this;
    }

    @Override
    public String toString() {
        return "SimpleActionResult{" +
                "name='" + name + '\'' +
                ", booleanState=" + booleanState +
                '}';
    }
}
