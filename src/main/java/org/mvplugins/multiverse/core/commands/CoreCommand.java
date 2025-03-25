package org.mvplugins.multiverse.core.commands;

import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;

import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.MultiverseCommand;

/**
 * Represents a command that is part of the Multiverse-Core plugin.
 */
@Contract
public abstract class CoreCommand extends MultiverseCommand {
    protected CoreCommand(@NotNull MVCommandManager commandManager) {
        super(commandManager, "mv");
    }
}
