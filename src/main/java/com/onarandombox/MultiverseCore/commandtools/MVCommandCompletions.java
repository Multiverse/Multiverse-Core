package com.onarandombox.MultiverseCore.commandtools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.PaperCommandCompletions;
import com.google.common.collect.Sets;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import org.bukkit.GameRule;
import org.jetbrains.annotations.NotNull;

public class MVCommandCompletions extends PaperCommandCompletions {
    protected final MVCommandManager commandManager;
    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;

    public MVCommandCompletions(MVCommandManager mvCommandManager, MultiverseCore plugin) {
        super(mvCommandManager);
        this.commandManager = mvCommandManager;
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();

        registerAsyncCompletion("destinations", this::suggestDestinations);
        registerAsyncCompletion("flags", this::suggestFlags);
        registerStaticCompletion("gamerules", this::suggestGamerules);
        registerAsyncCompletion("mvworlds", this::suggestMVWorlds);

        setDefaultCompletion("destinations", ParsedDestination.class);
        setDefaultCompletion("flags", String[].class);
        setDefaultCompletion("gamerules", GameRule.class);
        setDefaultCompletion("mvworlds", MVWorld.class);
    }

    private Collection<String> suggestDestinations(BukkitCommandCompletionContext context) {
        if (context.hasConfig("playerOnly") && !context.getIssuer().isPlayer()) {
            return Collections.emptyList();
        }

        return this.plugin.getDestinationsProvider()
                .suggestDestinations((BukkitCommandIssuer)context.getIssuer(), context.getInput());
    }

    private Collection<String> suggestFlags(@NotNull BukkitCommandCompletionContext context) {
        return this.commandManager.getFlagsManager().suggest(
                context.getConfig("groupName", ""), context.getContextValue(String[].class));
    }

    private Collection<String> suggestGamerules() {
        return Arrays.stream(GameRule.values()).map(GameRule::getName).collect(Collectors.toList());
    }

    private Collection<String> suggestMVWorlds(BukkitCommandCompletionContext context) {
        if (context.hasConfig("playerOnly") && !context.getIssuer().isPlayer()) {
            return Collections.emptyList();
        }

        if (!context.hasConfig("multiple")) {
            return getMVWorldNames(context);
        }

        String input = context.getInput();
        int lastComma = input.lastIndexOf(',');
        String currentWorldsString = input.substring(0, lastComma + 1);
        Set<String> currentWorlds = Sets.newHashSet(input.split(","));
        return getMVWorldNames(context).stream()
                .filter(world -> !currentWorlds.contains(world))
                .map(world -> currentWorldsString + world)
                .collect(Collectors.toList());
    }

    private List<String> getMVWorldNames(BukkitCommandCompletionContext context) {
        String scope = context.getConfig("scope", "loaded");
        List<String> worlds = new ArrayList<>();
        switch (scope) {
            case "both":
                worlds.addAll(worldManager.getUnloadedWorlds());
            case "loaded":
                worldManager.getMVWorlds()
                        .stream()
                        .map(MVWorld::getName)
                        .forEach(worlds::add);
                break;
            case "unloaded":
                worlds.addAll(worldManager.getUnloadedWorlds());
                break;
            case "potential":
                worlds.addAll(worldManager.getPotentialWorlds());
                break;
        }
        return worlds;
    }
}
