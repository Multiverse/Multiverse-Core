package com.onarandombox.MultiverseCore.commandtools.flags;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlagGroup {

    public static FlagGroup of(CommandFlag<?>...flags) {
        return new FlagGroup(flags);
    }

    private final List<String> flagIdentifiers;
    private final Map<String, CommandFlag<?>> flagKeyMap;

    private FlagGroup(CommandFlag<?>[] commandFlags) {
        this.flagIdentifiers = new ArrayList<>(commandFlags.length);
        this.flagKeyMap = new HashMap<>();
        for (CommandFlag<?> flag : commandFlags) {
            addFlag(flag);
        }
    }

    private void addFlag(CommandFlag<?> flag) {
        this.flagIdentifiers.add(flag.getIdentifier());
        this.flagKeyMap.put(flag.getIdentifier(), flag);
        for (String flagAlias : flag.getAliases()) {
            this.flagKeyMap.put(flagAlias, flag);
        }
    }

    @NotNull
    public FlagResult calculateResult(String[] args) {
        return FlagResult.parse(args,this);
    }

    @Nullable
    public CommandFlag<?> getByKey(String key) {
        return this.flagKeyMap.get(key);
    }

    @NotNull
    public Collection<String> getFlagIdentifiers() {
        return flagIdentifiers;
    }

    @Override
    public String toString() {
        return "FlagGroup{" +
                "flagIdentifiers=" + flagIdentifiers +
                ", keyFlagMap=" + flagKeyMap +
                '}';
    }
}
