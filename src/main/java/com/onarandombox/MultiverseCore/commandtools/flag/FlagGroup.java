package com.onarandombox.MultiverseCore.commandtools.flag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A group of {@link CommandFlag}, with indexed keys for efficient lookup.
 */
public class FlagGroup {

    /**
     * Create a new Flag Group with multiple {@link CommandFlag}.
     *
     * @param flags Multiple flags.
     * @return A new {@link FlagGroup} generated from the flags.
     */
    public static FlagGroup of(CommandFlag<?>...flags) {
        return new FlagGroup(flags);
    }

    private final List<String> identifiers;
    private final Map<String, CommandFlag<?>> keyMap;

    /**
     * Create a new Flag Group with multiple {@link CommandFlag}.
     *
     * @param commandFlags Array of flags
     */
    public FlagGroup(CommandFlag<?>[] commandFlags) {
        this.identifiers = new ArrayList<>(commandFlags.length);
        this.keyMap = new HashMap<>();
        for (CommandFlag<?> flag : commandFlags) {
            addFlag(flag);
        }
    }

    /**
     * Add and indexes a flag.
     *
     * @param flag  The flag to add.
     */
    private void addFlag(CommandFlag<?> flag) {
        this.identifiers.add(flag.getIdentifier());
        this.keyMap.put(flag.getIdentifier(), flag);
        for (String flagAlias : flag.getAliases()) {
            this.keyMap.put(flagAlias, flag);
        }
    }

    /**
     * Parse the arguments to get it's flag values.
     *
     * @param args  The arguments to parse.
     * @return A {@link FlagResult} containing value results.
     * @throws FlagParseFailedException When there is an error parsing, such as invalid format.
     */
    @NotNull
    public FlagResult calculateResult(String[] args) throws FlagParseFailedException {
        return FlagResult.parse(args,this);
    }

    /**
     * Gets flag from pre-indexed key mapping.
     *
     * @param key   The target key.
     * @return A {@link CommandFlag} if found, else null.
     */
    @Nullable
    public CommandFlag<?> getByKey(String key) {
        return this.keyMap.get(key);
    }

    /**
     * Suggest possible identifiers available for this Flag Group.
     *
     * @return A collection of identifier strings.
     */
    @NotNull
    public Collection<String> suggestIdentifiers() {
        return identifiers;
    }

    @Override
    public String toString() {
        return "FlagGroup{" +
                "flagIdentifiers=" + identifiers +
                ", keyFlagMap=" + keyMap +
                '}';
    }
}
