package org.mvplugins.multiverse.core.commandtools;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.PaperCommandCompletions;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.RootCommand;
import com.dumptruckman.minecraft.util.Logging;
import com.google.common.collect.Sets;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.GameRule;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.destination.ParsedDestination;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.MultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.WorldManager;

@Service
public class MVCommandCompletions extends PaperCommandCompletions {

    protected final MVCommandManager commandManager;
    private final WorldManager worldManager;
    private final DestinationsProvider destinationsProvider;

    @Inject
    public MVCommandCompletions(
            @NotNull MVCommandManager mvCommandManager,
            @NotNull WorldManager worldManager,
            @NotNull DestinationsProvider destinationsProvider,
            @NotNull MVCoreConfig config
            ) {
        super(mvCommandManager);
        this.commandManager = mvCommandManager;
        this.worldManager = worldManager;
        this.destinationsProvider = destinationsProvider;

        registerAsyncCompletion("commands", this::suggestCommands);
        registerAsyncCompletion("destinations", this::suggestDestinations);
        registerAsyncCompletion("flags", this::suggestFlags);
        registerStaticCompletion("gamerules", this::suggestGamerules);
        registerStaticCompletion("mvconfigs", config.getNodes().getNames());
        registerAsyncCompletion("mvworlds", this::suggestMVWorlds);

        setDefaultCompletion("destinations", ParsedDestination.class);
        setDefaultCompletion("flags", String[].class);
        setDefaultCompletion("gamerules", GameRule.class);
        setDefaultCompletion("mvworlds", LoadedMultiverseWorld.class);
    }

    private Collection<String> suggestCommands(BukkitCommandCompletionContext context) {
        String rootCmdName = context.getConfig();
        if (rootCmdName == null) {
            return Collections.emptyList();
        }

        RootCommand rootCommand = this.commandManager.getRegisteredRootCommands().stream()
                .unordered()
                .filter(c -> c.getCommandName().equals(rootCmdName))
                .findFirst()
                .orElse(null);

        if (rootCommand == null) {
            return Collections.emptyList();
        }

        return rootCommand.getSubCommands().entries().stream()
                .filter(entry -> checkPerms(context.getIssuer(), entry.getValue()))
                .map(Map.Entry::getKey)
                .filter(cmdName -> !cmdName.startsWith("__"))
                .collect(Collectors.toList());
    }

    private boolean checkPerms(CommandIssuer issuer, RegisteredCommand<?> command) {
        return this.commandManager.hasPermission(issuer, command.getRequiredPermissions());
    }

    private Collection<String> suggestDestinations(BukkitCommandCompletionContext context) {
        if (context.hasConfig("playerOnly") && !context.getIssuer().isPlayer()) {
            return Collections.emptyList();
        }

        return this.destinationsProvider
                .suggestDestinations((BukkitCommandIssuer)context.getIssuer(), context.getInput());
    }

    private Collection<String> suggestFlags(@NotNull BukkitCommandCompletionContext context) {
        String groupName = context.getConfig("groupName", "");

        return Try.of(() -> context.getContextValue(String[].class))
                .map(flags -> commandManager.getFlagsManager().suggest(groupName, flags))
                .getOrElse(Collections.emptyList());
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
        switch (scope) {
            case "both" -> {
                return worldManager.getWorlds().stream().map(MultiverseWorld::getName).toList();
            }
            case "loaded" -> {
                return worldManager.getLoadedWorlds()
                        .stream()
                        .map(LoadedMultiverseWorld::getName)
                        .toList();
            }
            case "unloaded" -> {
                return worldManager.getUnloadedWorlds().stream()
                        .map(MultiverseWorld::getName)
                        .toList();
            }
            case "potential" -> {
                return worldManager.getPotentialWorlds();
            }
        }
        Logging.severe("Invalid MVWorld scope: " + scope);
        return Collections.emptyList();
    }
}
