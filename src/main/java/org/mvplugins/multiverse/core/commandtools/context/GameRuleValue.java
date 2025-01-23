package org.mvplugins.multiverse.core.commandtools.context;

/**
 * Simple wrapper for game rule value, as they may be different types.
 */
public final class GameRuleValue {
    private final Object value;

    public GameRuleValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
