package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;

/**
 * Main class to manage permissions.
 */
public class MVCommandManager extends PaperCommandManager {

    public MVCommandManager(MultiverseCore plugin) {
        super(plugin);
    }

    /**
     * Gets class responsible for parsing string args into objects.
     *
     * @return A not-null {@link CommandContexts}.
     */
    @Override
    public synchronized CommandContexts<BukkitCommandExecutionContext> getCommandContexts() {
        if (this.contexts == null) {
            this.contexts = new MVCommandParser(this);
        }
        return this.contexts;
    }

    /**
     * Gets class responsible for tab-completion handling.
     *
     * @return A not-null {@link CommandCompletions}.
     */
    @Override
    public synchronized CommandCompletions<BukkitCommandCompletionContext> getCommandCompletions() {
        if (this.completions == null) {
            this.completions = new MVCommandSuggestion(this);
        }
        return this.completions;
    }

    /**
     * Registers an {@link MultiverseCommand}.
     *
     * @param command   Command to register. This need to extend {@link MultiverseCommand}.
     * @param force     Specify of command should be forcefully registered.
     */
    @Override
    public void registerCommand(BaseCommand command, boolean force) {
        ensureIsMultiverseCommand(command);
        super.registerCommand(command, force);
    }

    /**
     * Registers an {@link MultiverseCommand} without force.
     *
     * @param command   Command to register. This need to extend {@link MultiverseCommand}.
     */
    @Override
    public void registerCommand(BaseCommand command) {
        ensureIsMultiverseCommand(command);
        super.registerCommand(command);
    }

    /**
     * Unregister a command from Multiverse's knowledge.
     *
     * @param command   Command to unregister. This need to extend {@link MultiverseCommand}.
     */
    @Override
    public void unregisterCommand(BaseCommand command) {
        ensureIsMultiverseCommand(command);
        super.unregisterCommand(command);
    }

    /**
     * Validate that the {@link BaseCommand} is an extension of {@link MultiverseCommand}.
     *
     * @param command   The command to check on.
     */
    private void ensureIsMultiverseCommand(BaseCommand command) {
        if (!(command instanceof MultiverseCommand)) {
            throw new IllegalArgumentException(command.getName() + " is not a MultiverseCommand!");
        }
    }
}
