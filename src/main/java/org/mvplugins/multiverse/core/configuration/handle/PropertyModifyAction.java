package org.mvplugins.multiverse.core.configuration.handle;

/**
 * The type of modification to a config.
 */
public enum PropertyModifyAction {
    /**
     * Sets a new value based on the provided input.
     */
    SET(true),

    /**
     * Add an item to a list. Only applies to values of type List.
     */
    ADD(true),

    /**
     * Remove an item from a list. Only applies to values of type List.
     */
    REMOVE(true),

    /**
     * Reset the value to the default.
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
     */
    public boolean isRequireValue() {
        return requireValue;
    }
}
