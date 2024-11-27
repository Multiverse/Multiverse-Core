package org.mvplugins.multiverse.core.configuration.handle;

/**
 * The type of modification to a config.
 */
public enum PropertyModifyAction {
    SET(true),
    ADD(true),
    REMOVE(true),
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
