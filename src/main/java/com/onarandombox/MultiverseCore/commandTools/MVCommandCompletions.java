/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commandTools;

import buscript.Buscript;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.PaperCommandCompletions;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.RootCommand;
import com.dumptruckman.minecraft.util.Logging;
import com.google.common.collect.Lists;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandTools.contexts.Flag;
import com.onarandombox.MultiverseCore.enums.AddProperties;
import com.onarandombox.MultiverseCore.enums.FlagValue;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceType;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Console;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generate tab-complete suggestion.
 */
public class MVCommandCompletions extends PaperCommandCompletions {

    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;

    private static final DecimalFormat df = new DecimalFormat();

    static {
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(2);
    }

    public MVCommandCompletions(@NotNull MVCommandManager manager,
                                @NotNull MultiverseCore plugin) {
        super(manager);
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();

        registerAsyncCompletion("worldFlags", this::suggestWorldFlags);
        registerAsyncCompletion("scripts", this::suggestScripts);
        registerAsyncCompletion("subCommands", this::suggestSubCommands);
        registerAsyncCompletion("MVWorlds", this::suggestMVWorlds);
        registerAsyncCompletion("unloadedWorlds", this::suggestUnloadedWorlds);
        registerAsyncCompletion("potentialWorlds", this::suggestPotentialWorlds);
        registerAsyncCompletion("location", this::suggestLocation);
        registerAsyncCompletion("destinations", this::suggestDestinations);
        registerAsyncCompletion("anchors", this::suggestAnchors);
        registerStaticCompletion("MVConfigs", this::suggestMVConfig);
        registerStaticCompletion("gameRules", this::suggestGameRules);
        registerStaticCompletion("environments", this::suggestEnvironments);
        registerStaticCompletion("setProperties", this::suggestSetProperties);
        registerStaticCompletion("addProperties", this::suggestAddProperties);
        registerStaticCompletion("livingEntities", this::suggestEntities);
        registerStaticCompletion("pasteTypes", this::suggestPasteTypes);
        registerStaticCompletion("toggles", this::suggestToggles);
    }

    @NotNull
    private Collection<String> suggestWorldFlags(@NotNull BukkitCommandCompletionContext context) {
        List<String> args = Arrays.asList(context.getContextValue(String[].class));
        Set<String> flagsKeys = new HashSet<>(Arrays.asList(context.getConfig().split(",")));

        String mostRecentArg = (args.isEmpty()) ? null : args.get(args.size() - 1);
        Flag<?> flag = Flag.getByKey(mostRecentArg);
        if (flag == null) {
            flagsKeys.removeAll(args);
            return flagsKeys;
        }

        if (!flagsKeys.contains(mostRecentArg)) {
            return Collections.emptyList();
        }

        switch (flag.getValueRequirement()) {
            case REQUIRED:
                return flag.suggestValue();
            case OPTIONAL:
                Collection<String> suggestions = flag.suggestValue();
                flagsKeys.removeAll(args);
                suggestions.addAll(flagsKeys);
                return suggestions;
            case NONE:
                flagsKeys.removeAll(args);
                return flagsKeys;
        }

        return Collections.emptyList();
    }



    @NotNull
    private Collection<String> suggestScripts(@NotNull BukkitCommandCompletionContext context) {
        Buscript scriptAPI = this.plugin.getScriptAPI();
        if (scriptAPI == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(scriptAPI.getScriptFolder().listFiles())
                .unordered()
                .filter(File::isFile)
                .map(File::getName)
                .filter(fileName -> !fileName.equals("scripts.bin"))
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestSubCommands(@NotNull BukkitCommandCompletionContext context) {
        String rootCmdName = context.getConfig();
        if (rootCmdName == null) {
            return Collections.emptyList();
        }

        RootCommand rootCommand = this.plugin.getMVCommandManager().getRegisteredRootCommands().stream()
                .unordered()
                .filter(c -> c.getCommandName().equals(rootCmdName))
                .findFirst()
                .orElse(null);

        if (rootCommand == null) {
            return Collections.emptyList();
        }

        return rootCommand.getSubCommands().entries().stream()
                .unordered()
                .filter(entry -> checkPerms(context.getIssuer(), entry.getValue()))
                .map(Map.Entry::getKey)
                .filter(cmdName -> !cmdName.startsWith("__"))
                .collect(Collectors.toList());
    }

    private boolean checkPerms(@NotNull CommandIssuer issuer,
                               @NotNull RegisteredCommand<?> cmd) {

        return this.plugin.getMVCommandManager().hasPermission(issuer, cmd.getRequiredPermissions());
    }

    @NotNull
    private Collection<String> suggestMVWorlds(@NotNull BukkitCommandCompletionContext context) {
        if (isPlayerOnly(context)) {
            return Collections.emptyList();
        }

        return this.worldManager.getMVWorlds().parallelStream()
                .unordered()
                .map(MultiverseWorld::getName)
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestUnloadedWorlds(@NotNull BukkitCommandCompletionContext context) {
        if (isPlayerOnly(context)) {
            return Collections.emptyList();
        }

        return this.worldManager.getUnloadedWorlds();
    }

    @NotNull
    private Collection<String> suggestPotentialWorlds(@NotNull BukkitCommandCompletionContext context) {
        if (isPlayerOnly(context)) {
            return Collections.emptyList();
        }

        Collection<MultiverseWorld> worlds = this.worldManager.getMVWorlds();
        Set<String> knownWorlds = worlds.parallelStream()
                .unordered()
                .map(MultiverseWorld::getName)
                .collect(Collectors.toCollection(() -> new HashSet<>(worlds.size())));

        return Arrays.stream(this.plugin.getServer().getWorldContainer().listFiles()).parallel()
                .unordered()
                .filter(File::isDirectory)
                .filter(file -> !knownWorlds.contains(file.getName()))
                .map(File::getName)
                .filter(this.worldManager::isValidWorld)
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestLocation(@NotNull BukkitCommandCompletionContext context) {
        if (isPlayerOnly(context)) {
            return Collections.emptyList();
        }

        Player player = context.getPlayer();
        if (player == null) {
            return Collections.emptyList();
        }

        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(2);

        Location playerLocation = player.getLocation();
        double coordValue;
        switch (context.getConfig()) {
            case "x":
                coordValue = playerLocation.getX();
                break;
            case "y":
                coordValue = playerLocation.getY();
                break;
            case "z":
                coordValue = playerLocation.getZ();
                break;
            case "yaw":
                coordValue = playerLocation.getYaw();
                break;
            case "pitch":
                coordValue = playerLocation.getPitch();
                break;
            default:
                return Collections.emptyList();
        }

        return Collections.singletonList(df.format(coordValue));
    }

    @NotNull
    private Collection<String> suggestDestinations(@NotNull BukkitCommandCompletionContext context) {
        if (isPlayerOnly(context)) {
            return Collections.emptyList();
        }

        return this.plugin.getDestFactory().getIdentifiers().parallelStream()
                .unordered()
                .filter(id -> !id.isEmpty())
                .map(id -> id + ":")
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestAnchors(@NotNull BukkitCommandCompletionContext context) {
        if (isPlayerOnly(context)) {
            return Collections.emptyList();
        }

        return this.plugin.getAnchorManager().getAnchors(context.getPlayer());
    }

    private boolean isPlayerOnly(@NotNull BukkitCommandCompletionContext context) {
        String config = context.getConfig();
        return config != null && context.getPlayer() == null && config.equals("playerOnly");
    }

    @NotNull
    private Collection<String> suggestMVConfig() {
        final Set<String> configOptions = this.plugin.getMVConfig().serialize().keySet();
        configOptions.remove("version");
        return configOptions;
    }

    @NotNull
    private Collection<String> suggestGameRules() {
        return Arrays.stream(GameRule.values())
                .map(GameRule::getName)
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestEnvironments() {
        return Arrays.stream(World.Environment.values())
                .map(e -> e.toString().toLowerCase())
                .collect(Collectors.toList());
    }

    private Collection<String> suggestSetProperties() {
        return this.worldManager.getMVWorlds().iterator().next().getAllPropertyTypes();
    }

    @NotNull
    private Collection<String> suggestAddProperties() {
        return Arrays.stream(AddProperties.values())
                .map(p -> p.toString().toLowerCase())
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestEntities() {
        return Arrays.stream(EntityType.values())
                .filter(e -> e.isAlive() && e.isSpawnable())
                .map(e -> e.toString().toLowerCase())
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestPasteTypes() {
        return Arrays.stream(PasteServiceType.values())
                .filter(pt -> pt != PasteServiceType.GITHUB && pt != PasteServiceType.NONE)
                .map(p -> p.toString().toLowerCase())
                .collect(Collectors.toList());
    }

    @NotNull
    private Collection<String> suggestToggles() {
        return Arrays.asList("on", "off");
    }
}
