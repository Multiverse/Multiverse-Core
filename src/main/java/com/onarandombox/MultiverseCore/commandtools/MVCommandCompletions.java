package com.onarandombox.MultiverseCore.commandtools;

import java.util.Collection;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.PaperCommandCompletions;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.jetbrains.annotations.NotNull;

public class MVCommandCompletions extends PaperCommandCompletions {
    protected final MVCommandManager commandManager;
    private final MultiverseCore plugin;

    public MVCommandCompletions(MVCommandManager mvCommandManager, MultiverseCore plugin) {
        super(mvCommandManager);
        this.commandManager = mvCommandManager;
        this.plugin = plugin;

        registerAsyncCompletion("destinations", this::suggestDestinations);
        registerAsyncCompletion("flags", this::suggestFlags);
        registerAsyncCompletion("mvworlds", this::suggestMVWorlds);
    }

    private Collection<String> suggestDestinations(BukkitCommandCompletionContext context) {
        return this.plugin.getDestinationsManager()
                .suggestDestinations((BukkitCommandIssuer)context.getIssuer(), context.getInput());
    }

    private Collection<String> suggestFlags(@NotNull BukkitCommandCompletionContext context) {
        return this.commandManager.getFlagsManager().suggest(
                context.getConfig("groupName", ""), context.getContextValue(String[].class));
    }

    private Collection<String> suggestMVWorlds(BukkitCommandCompletionContext context) {
        return this.plugin.getMVWorldManager().getMVWorlds()
                .stream()
                .map(MultiverseWorld::getName)
                .collect(Collectors.toList());
    }
}
