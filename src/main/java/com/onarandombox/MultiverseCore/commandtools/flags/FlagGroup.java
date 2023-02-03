package com.onarandombox.MultiverseCore.commandtools.flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlagGroup {
    public static Builder builder(String name) {
        return new Builder(name);
    }

    private final String name;
    private final List<MVFlag> flags;
    private final List<String> keys;
    private final Map<String, MVFlag> keysFlagMap;

    protected FlagGroup(Builder builder) {
        name = builder.name;
        flags = builder.flags;
        keys = builder.keys;
        keysFlagMap = builder.keysFlagMap;
    }

    public String getName() {
        return name;
    }

    public List<MVFlag> getFlags() {
        return flags;
    }

    public List<String> getKeys() {
        return keys;
    }

    public Map<String, MVFlag>getKeysFlagMap() {
        return keysFlagMap;
    }

    public MVFlag getFlagByKey(String key) {
        return keysFlagMap.get(key);
    }

    public static class Builder {
        private final String name;
        private final List<MVFlag> flags;
        private final List<String> keys;
        private final Map<String, MVFlag> keysFlagMap;

        public Builder(String name) {
            this.name = name;
            this.flags = new ArrayList<>();
            this.keys = new ArrayList<>();
            this.keysFlagMap = new HashMap<>();
        }

        public Builder add(MVFlag flag) {
            flags.add(flag);
            keys.add(flag.getKey());
            keysFlagMap.put(flag.getKey(), flag);
            flag.getAliases().forEach((alias) -> keysFlagMap.put(alias, flag));
            return this;
        }

        public FlagGroup build() {
            return new FlagGroup(this);
        }
    }
}
