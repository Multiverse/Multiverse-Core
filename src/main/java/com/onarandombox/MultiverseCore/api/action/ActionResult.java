package com.onarandombox.MultiverseCore.api.action;

/**
 * Represents one or more action result.
 */
public interface ActionResult {
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
    boolean isSuccessful();

    /**
     * Check if the result is in the response.
     *
     * @param result    The result to check.
     * @return true if the result is in the response.
     */
    boolean hasResult(ActionResult result);
}
