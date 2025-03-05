package org.mvplugins.multiverse.core.commandtools.flag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import co.aikar.commands.InvalidCommandArgument;
import com.dumptruckman.minecraft.util.Logging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

/**
 * Manages all the flag groups and parsing.
 */
@Service
public class CommandFlagsManager {
    private final Map<String, CommandFlagGroup> flagGroupMap;

    /**
     * Creates a new FlagsManager.
     */
    public CommandFlagsManager() {
        flagGroupMap = new HashMap<>();
    }

    /**
     * Registers a flag group.
     *
     * @param flagGroup The target flag group to register.
     */
    public void registerFlagGroup(@NotNull CommandFlagGroup flagGroup) {
        flagGroupMap.put(flagGroup.getName(), flagGroup);
    }

    /**
     *  Get a flag group by name.
     *
     * @param groupName The target flag group name.
     * @return The flag group if found, null otherwise.
     */
    public @Nullable CommandFlagGroup getFlagGroup(@Nullable String groupName) {
        return this.flagGroupMap.get(groupName);
    }

    /**
     * Autocompletes suggestions for flags.
     *
     * @param groupName The target flag group name.
     * @param flags The current flags so far.
     * @return The list of suggestions.
     */
    public @NotNull Collection<String> suggest(@Nullable String groupName, @NotNull String[] flags) {
        CommandFlagGroup flagGroup = this.getFlagGroup(groupName);
        if (flagGroup == null) {
            Logging.warning("Unknown flag group: " + groupName);
            return Collections.emptyList();
        }

        Collection<String> suggestions = new ArrayList<>();
        CommandFlag currentFlag = (flags.length <= 1) ? null : flagGroup.getFlagByKey(flags[flags.length - 2]);

        if (currentFlag instanceof CommandValueFlag<?> valueFlag) {
            if (valueFlag.getCompletion() != null) {
                suggestions.addAll(valueFlag.getCompletion().apply(flags[flags.length - 1]));
            }
            if (valueFlag.isOptional()) {
                suggestions.addAll(flagGroup.getRemainingKeys(flags));
            }
        } else {
            suggestions.addAll(flagGroup.getRemainingKeys(flags));
        }

        return suggestions;
    }

    /**
     * Parses the flags.
     *
     * @param groupName The target flag group name.
     * @param flags The flags to parse.
     * @return The parsed flags.
     *
     * @throws InvalidCommandArgument If the flags are invalid.
     */
    public @NotNull ParsedCommandFlags parse(@Nullable String groupName, @NotNull String[] flags) {
        CommandFlagGroup flagGroup = this.getFlagGroup(groupName);
        if (flagGroup == null) {
            return ParsedCommandFlags.EMPTY;
        }

        return new CommandFlagsParser(this.getFlagGroup(groupName), flags).parse();
    }
}
