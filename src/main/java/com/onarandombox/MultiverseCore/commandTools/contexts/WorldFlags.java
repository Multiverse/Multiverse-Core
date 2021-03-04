package com.onarandombox.MultiverseCore.commandTools.contexts;

import co.aikar.commands.InvalidCommandArgument;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandTools.flag.Flag;
import com.onarandombox.MultiverseCore.commandTools.flag.Flags;
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
        Logging.finer(String.valueOf(this.inputFlagMap));
        Logging.finer(String.valueOf(this.flagMap));
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
            Flag<?> flag = Flags.getByKey(arg);
            if (currentFlag == null) {
                if (flag == null) {
                    throw new InvalidCommandArgument(String.format("'%s' is not a valid flag key.", arg));
                }
                currentFlag = flag;
            }
            if (flag == null) {
                inputFlagMap.put(currentFlag, arg);
                currentFlag = null;
            }
            inputFlagMap.put(currentFlag, null);
            currentFlag = flag;
        }

        if (currentFlag != null) {
            inputFlagMap.put(currentFlag, null);
        }
    }

    private void parseFlagMap(@NotNull MultiverseCore plugin,
                              @NotNull CommandSender sender) {

        flagMap = new HashMap<>();
        for (Flag<?> flag : wantedFlags) {
            if (inputFlagMap.containsKey(flag)) {
                flagMap.put(flag, flag.parseValue(inputFlagMap.get(flag), plugin, sender));
                continue;
            }
            flagMap.put(flag, flag.getDefault());
        }
    }

    public <T> T getValue(@NotNull Flag<T> flag) {
        return (T) flagMap.get(flag);
    }

    public boolean isNullValue(@NotNull Flag<?> flag) {
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
