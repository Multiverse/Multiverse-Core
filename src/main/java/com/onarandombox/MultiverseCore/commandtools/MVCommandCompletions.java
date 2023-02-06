package com.onarandombox.MultiverseCore.commandtools;

import java.util.Collection;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.PaperCommandCompletions;
import org.jetbrains.annotations.NotNull;

public class MVCommandCompletions extends PaperCommandCompletions {
    protected final MVCommandManager commandManager;

    public MVCommandCompletions(MVCommandManager mvCommandManager) {
        super(mvCommandManager);
        this.commandManager = mvCommandManager;

        registerAsyncCompletion("flags", this::suggestFlags);
    }

    @NotNull
    private Collection<String> suggestFlags(@NotNull BukkitCommandCompletionContext context) {
        return this.commandManager.getFlagsManager().suggest(
                context.getConfig("groupName", ""), context.getContextValue(String[].class));
    }
}
