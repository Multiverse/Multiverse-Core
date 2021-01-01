/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools.contexts;

import co.aikar.commands.InvalidCommandArgument;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commands.EnvironmentCommand;
import com.onarandombox.MultiverseCore.commands.GeneratorCommand;
import org.bukkit.ChatColor;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Additional world settings.
 */
public class WorldFlags {
    private final String seed;
    private final String generator;
    private final WorldType worldType;
    private final boolean spawnAdjust;
    private final boolean generateStructures;
    private final Map<String, String> parsedFlags;

    private static final Set<String> FLAG_KEYS = Collections.unmodifiableSet(new HashSet<String>() {{
        add("-s");
        add("-g");
        add("-t");
        add("-n");
        add("-a");
    }});

    public WorldFlags(@NotNull CommandSender sender,
                      @NotNull MultiverseCore plugin,
                      @Nullable String[] args) {

        Map<String, String> flags = parseFlags(args);
        Logging.finer("World flags: " + flags.toString());

        this.parsedFlags = flags;
        this.seed = flags.get("-s");
        this.generator = validateGenerator(flags.get("-g"), sender, plugin);
        this.worldType = getWorldType(flags.get("-t"), sender);
        this.spawnAdjust = !flags.containsKey("-n");
        this.generateStructures = doGenerateStructures(flags.get("-a"));
    }

    /**
     *
     * @param genString  Generator setting.
     * @param sender     Command executor.
     * @param plugin     Multiverse plugin.
     * @return genString if generator is valid, null otherwise.
     */
    @Nullable
    private String validateGenerator(@Nullable String genString,
                                     @NotNull CommandSender sender,
                                     @NotNull MultiverseCore plugin) {

        if (genString == null) {
            return null;
        }

        String[] genArray = genString.split(":");
        if (genArray.length == 0) {
            return null;
        }

        String generator = genArray[0];
        String generatorId = (genArray.length > 1) ? genArray[1] : "";

        try {
            if (plugin.getMVWorldManager().getChunkGenerator(generator, generatorId, "test") == null) {
                sender.sendMessage(ChatColor.RED + "Invalid generator string '" + genString + "'.");
                GeneratorCommand.showAvailableGenerator(sender);
                throw new InvalidCommandArgument(false);
            }
        }
        catch (Exception e) {
            sender.sendMessage(String.format("%sThere was an error creating world with generator '%s'! Please check console for errors.",
                    ChatColor.RED, genString));
            e.printStackTrace();
            Logging.severe("Error occurred when trying to create your world with generator '%s'. Reason: %s",
                    genString, e.getCause());
            throw new InvalidCommandArgument(false);
        }

        return genString;
    }

    @NotNull
    private WorldType getWorldType(@Nullable String type,
                                   @NotNull CommandSender sender) {

        if (type == null || type.length() == 0) {
            return WorldType.NORMAL;
        }

        if (type.equalsIgnoreCase("normal")) {
            type = "NORMAL";
        }
        else if (type.equalsIgnoreCase("flat")) {
            type = "FLAT";
        }
        else if (type.equalsIgnoreCase("largebiomes")) {
            type = "LARGE_BIOMES";
        }
        else if (type.equalsIgnoreCase("amplified")) {
            type = "AMPLIFIED";
        }

        try {
            return WorldType.valueOf(type);
        }
        catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "'" + type + "' is not a valid World Type.");
            EnvironmentCommand.showWorldTypes(sender);
            throw new InvalidCommandArgument(false);
        }
    }

    private boolean doGenerateStructures(@Nullable String value) {
        return value == null || value.equalsIgnoreCase("true");
    }

    /**
     * Parse world setting flags into it's key value pair.
     *
     * @param args Array of string to parse.
     * @return Map of flag and value pair.
     */
    @NotNull
    private Map<String, String> parseFlags(@Nullable String[] args) {
        Map<String, String> flags = new HashMap<>();
        if (args == null || args.length == 0) {
            return flags;
        }

        mapOutTheArgs(args, flags);
        return flags;
    }

    private void mapOutTheArgs(@NotNull String[] args,
                               @NotNull Map<String, String> flags) {

        String currentFlagKey = null;

        for (String arg : args) {
            if (isValidFlagKey(arg)) {
                if (currentFlagKey != null) {
                    flags.put(currentFlagKey, null);
                }
                currentFlagKey = arg;
                continue;
            }

            if (currentFlagKey == null) {
                throw new InvalidCommandArgument("'" + arg + "' is not a valid flag key.");
            }
            flags.put(currentFlagKey, arg);
            currentFlagKey = null;
        }

        if (currentFlagKey != null) {
            flags.put(currentFlagKey, null);
        }
    }

    private boolean isValidFlagKey(@Nullable String value) {
        return value != null && FLAG_KEYS.contains(value.toLowerCase());
    }

    public boolean hasFlag(String flag) {
        return parsedFlags.containsKey(flag);
    }

    public String getSeed() {
        return seed;
    }

    public String getGenerator() {
        return generator;
    }

    public WorldType getWorldType() {
        return worldType;
    }

    public boolean isSpawnAdjust() {
        return spawnAdjust;
    }

    public boolean isGenerateStructures() {
        return generateStructures;
    }

    @Override
    public String toString() {
        return "CreateWorldFlags{" +
                "seed='" + seed + '\'' +
                ", generator='" + generator + '\'' +
                ", worldType=" + worldType +
                ", spawnAdjust=" + spawnAdjust +
                ", generateStructures=" + generateStructures +
                '}';
    }
}
