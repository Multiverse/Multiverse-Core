package com.onarandombox.MultiverseCore.commandtools.flags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MVFlag {
    public static Builder builder(String key){
        return new Builder(key);
    }

    private String key;
    private final List<String> aliases;

    protected MVFlag(Builder builder) {
        key = builder.key;
        aliases = builder.aliases;
    }

    public MVFlag() {
        aliases = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public static class Builder<S extends Builder> {
        private final String key;
        private final List<String> aliases;

        public Builder(String key) {
            this.key = key;
            aliases = new ArrayList<>();
        }

        public S addAlias(String...alias){
            Collections.addAll(this.aliases, alias);
            return (S) this;
        }

        public MVFlag build(){
            return new MVFlag(this);
        }
    }
}
