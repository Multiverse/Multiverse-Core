package com.onarandombox.MultiverseCore.commandTools.flags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlagGroup {

    public static FlagGroup of(CommandFlag<?>...flags) {
        return new FlagGroup(flags);
    }

    private final Set<CommandFlag<?>> flags;
    private final List<String> flagIdentifiers;
    private final Map<String, CommandFlag<?>> keyFlagMap;

    private FlagGroup(CommandFlag<?>[] commandFlags) {
        this.flags = new HashSet<>(commandFlags.length);
        this.flagIdentifiers = new ArrayList<>(commandFlags.length);
        this.keyFlagMap = new HashMap<>();
        for (CommandFlag<?> flag : commandFlags) {
            addFlag(flag);
        }
    }

    private void addFlag(CommandFlag<?> flag) {
        this.flags.add(flag);
        this.flagIdentifiers.add(flag.getIdentifier());
        this.keyFlagMap.put(flag.getIdentifier(), flag);
        for (String flagAlias : flag.getAliases()) {
            this.keyFlagMap.put(flagAlias, flag);
        }
    }

    public CommandFlag<?> getByKey(String key) {
        return this.keyFlagMap.get(key);
    }

    public Collection<CommandFlag<?>> getFlags() {
        return this.flags;
    }

    public Collection<String> getFlagIdentifiers() {
        return flagIdentifiers;
    }

    @Override
    public String toString() {
        return "FlagGroup{" +
                "flags=" + flags +
                ", keyFlagMap=" + keyFlagMap +
                '}';
    }
}
