package com.onarandombox.MultiverseCore.commandtools;

import java.util.Locale;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagsManager;
import com.onarandombox.MultiverseCore.commandtools.queue.CommandQueueManager;
import org.jetbrains.annotations.NotNull;

/**
 * Main class to manage permissions.
 */
public class MVCommandManager extends PaperCommandManager {

    private final MultiverseCore plugin;
    private CommandFlagsManager flagsManager;
    private CommandQueueManager commandQueueManager;

    public MVCommandManager(@NotNull MultiverseCore plugin) {
        super(plugin);
        this.plugin = plugin;

        // Setup conditions
        MVCommandConditions.load(this, plugin);

        // Setup locale
        this.addSupportedLanguage(Locale.ENGLISH);
        this.locales.addMessageBundles("multiverse-core");
        this.locales.loadLanguages();
    }

    /**
     * Gets class responsible for flag handling.
     *
     * @return A not-null {@link CommandFlagsManager}.
     */
    public synchronized @NotNull CommandFlagsManager getFlagsManager() {
        if (this.flagsManager == null) {
            this.flagsManager = new CommandFlagsManager();
        }
        return flagsManager;
    }

    /**
     * Manager for command that requires /mv confirm before execution.
     *
     * @return A non-null {@link CommandQueueManager}.
     */
    public synchronized @NotNull CommandQueueManager getCommandQueueManager() {
        if (this.commandQueueManager == null) {
            this.commandQueueManager = new CommandQueueManager(this.plugin);
        }
        return commandQueueManager;
    }

    /**
     * Gets class responsible for parsing string args into objects.
     *
     * @return A not-null {@link CommandContexts}.
     */
    @Override
    public synchronized @NotNull CommandContexts<BukkitCommandExecutionContext> getCommandContexts() {
        if (this.contexts == null) {
            this.contexts = new MVCommandContexts(this, plugin);
        }
        return this.contexts;
    }

    /**
     * Gets class responsible for tab-completion handling.
     *
     * @return A not-null {@link CommandCompletions}.
     */
    @Override
    public synchronized @NotNull CommandCompletions<BukkitCommandCompletionContext> getCommandCompletions() {
        if (this.completions == null) {
            this.completions = new MVCommandCompletions(this, plugin);
        }
        return this.completions;
    }
}
