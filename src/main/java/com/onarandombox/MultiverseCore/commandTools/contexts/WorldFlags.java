package com.onarandombox.MultiverseCore.commandTools.contexts;

import co.aikar.commands.InvalidCommandArgument;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.enums.FlagValue;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WorldFlags {

    private final Collection<Flag<?>> wantedFlags;
    private Map<Flag<?>, Object> flagMap;
    private Map<Flag<?>, String> inputFlagMap;

    public WorldFlags(@NotNull MultiverseCore plugin,
                      @NotNull CommandSender sender,
                      @Nullable String[] args,
                      @NotNull Collection<Flag<?>> wantedFlags) {

        this.wantedFlags = wantedFlags;
        parseInputFlags(args);
        parseFlagMap(plugin, sender);
    }

    /**
     * Parse world setting flags into it's key value pair.
     *
     * @param args Array of string to parse.
     */
    private void parseInputFlags(@Nullable String[] args) {
        this.inputFlagMap = new HashMap<>();
        if (args == null || args.length == 0) {
            return;
        }

        Flag<?> currentFlag = null;
        for (String arg : args) {
            Flag<?> flag = Flag.getByKey(arg);

            // When it should be a value
            if (flag == null) {
                if (currentFlag == null) {
                    throw new InvalidCommandArgument(String.format("'%s' is not a valid flag key.", arg));
                }
                if (currentFlag.getValueRequirement() == FlagValue.NONE) {
                    throw new InvalidCommandArgument(String.format("'Flag %s' does not require a value.", arg));
                }
                inputFlagMap.put(currentFlag, arg);
                currentFlag = null;
                continue;
            }

            // When arg is a flag
            if (currentFlag != null) {
                if (currentFlag.getValueRequirement() == FlagValue.REQUIRED) {
                    throw new InvalidCommandArgument(String.format("You need to specify a value for flag '%s'.", flag.getKey()));
                }
                this.inputFlagMap.put(flag, null);
            }
            if (!this.wantedFlags.contains(flag)) {
                throw new InvalidCommandArgument(String.format("'%s' flag is not applicable for this command.", flag.getKey()));
            }
            currentFlag = flag;
        }
    }

    private void parseFlagMap(@NotNull MultiverseCore plugin,
                              @NotNull CommandSender sender) {

        flagMap = new HashMap<>();
        for (Flag<?> flag : wantedFlags) {
            if (inputFlagMap.containsKey(flag)) {
                flagMap.put(flag, flag.parseValue(inputFlagMap.get(flag), plugin, sender));
                return;
            }
            flagMap.put(flag, flag.getDefault());
        }
    }

    public <T> T getValue(@NotNull Flag<T> flag) {
        return (T) flagMap.get(flag);
    }

    public boolean hasNullValue(@NotNull Flag<?> flag) {
        return flagMap.get(flag) == null;
    }

    public boolean isByInput(@NotNull Flag<?> flag) {
        return inputFlagMap.containsKey(flag);
    }

    public Map<Flag<?>, Object> getFlagMap() {
        return flagMap;
    }

    public Map<Flag<?>, String> getInputFlagMap() {
        return inputFlagMap;
    }

    public Collection<Flag<?>> getWantedFlags() {
        return wantedFlags;
    }
}
