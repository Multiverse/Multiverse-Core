package org.mvplugins.multiverse.core.commandtools.context;

public class GameRuleValue {
    private final Object value;

    public GameRuleValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
