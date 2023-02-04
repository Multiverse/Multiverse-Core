package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BaseCommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagsManager;
import org.jetbrains.annotations.NotNull;

/**
 * A base command for Multiverse.
 */
public class MultiverseCommand extends BaseCommand {

    protected final MultiverseCore plugin;
    protected final MVWorldManager worldManager;
    protected final CommandFlagsManager flagsManager;

    protected MultiverseCommand(@NotNull MultiverseCore plugin) {
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();
        this.flagsManager = plugin.getCommandManager().getFlagsManager();
    }
}
