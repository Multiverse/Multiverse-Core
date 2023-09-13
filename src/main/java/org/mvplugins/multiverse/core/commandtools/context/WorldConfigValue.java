package org.mvplugins.multiverse.core.commandtools.context;

public class WorldConfigValue {
    private final Object value;

    public WorldConfigValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WorldConfigValue{"
                + "value=" + value
                + '}';
    }
}
