package com.onarandombox.MultiverseCore.api.operation;

import org.jetbrains.annotations.Nullable;

/**
 * Represents one or more action result.
 */
public interface OperationResult {
    /**
     * Get the name of the result.
     *
     * @return The name of the result.
     */
    String getName();

    /**
     * Check if the result is successful.
     *
     * @return true if the result is successful.
     */
    boolean asBoolean();

    /**
     * Check if the result is in the response.
     *
     * @param result    The result to check.
     * @return true if the result is in the response.
     */
    boolean hasResult(@Nullable OperationResult result);
}
