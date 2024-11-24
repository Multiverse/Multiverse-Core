package org.mvplugins.multiverse.core.commands;

import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;

@Contract
public abstract class CoreCommand extends MultiverseCommand {
    protected CoreCommand(@NotNull MVCommandManager commandManager) {
        super(commandManager);
    }
}