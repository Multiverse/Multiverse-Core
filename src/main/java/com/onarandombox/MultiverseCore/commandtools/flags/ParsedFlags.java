package com.onarandombox.MultiverseCore.commandtools.flags;

import java.util.HashMap;
import java.util.Map;

public class ParsedFlags
{
    private final Map<String, Object> flagValues;

    public ParsedFlags() {
        flagValues = new HashMap<>();
    }

    void addFlagResult(String key, Object value) {
        flagValues.put(key, value);
    }

    public boolean hasFlag(String key) {
        return this.flagValues.containsKey(key);
    }

    public <T> T flagValue(String key, Class<T> type) {
        Object value = this.flagValues.get(key);
        return (T)value;
    }
}
