package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;

/**
 * Generic multiverse core command with handy reference to the plugin instance.
 */
public abstract class MultiverseCoreCommand extends MultiverseCommand {

    protected final MultiverseCore plugin;

    protected MultiverseCoreCommand(MultiverseCore plugin) {
        this.plugin = plugin;
    }
}
