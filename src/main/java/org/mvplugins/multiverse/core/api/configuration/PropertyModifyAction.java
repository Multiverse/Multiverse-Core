package org.mvplugins.multiverse.core.api.configuration;

/**
 * The type of modification to a config.
 *
 * @since 5.0
 */
public enum PropertyModifyAction {
    /**
     * Sets a new value based on the provided input.
     *
     * @since 5.0
     */
    SET(true),

    /**
     * Add an item to a list. Only applies to values of type List.
     *
     * @since 5.0
     */
    ADD(true),

    /**
     * Remove an item from a list. Only applies to values of type List.
     *
     * @since 5.0
     */
    REMOVE(true),

    /**
     * Reset the value to the default.
     *
     * @since 5.0
     */
    RESET(false),
    ;

    private final boolean requireValue;

    PropertyModifyAction(boolean requireValue) {
        this.requireValue = requireValue;
    }

    /**
     * Whether this action requires a value.
     *
     * @return True if this action requires a value, false otherwise.
     * @since 5.0
     */
    public boolean isRequireValue() {
        return requireValue;
    }
}
