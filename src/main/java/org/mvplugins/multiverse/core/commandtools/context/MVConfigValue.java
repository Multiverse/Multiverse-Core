package org.mvplugins.multiverse.core.commandtools.context;

public class MVConfigValue {
    private final Object value;

    public MVConfigValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
