package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BaseCommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagsManager;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import org.jetbrains.annotations.NotNull;

/**
 * A base command for Multiverse.
 */
public class MultiverseCommand extends BaseCommand {
    protected final MultiverseCore plugin;
    protected final MVWorldManager worldManager;
    protected final CommandFlagsManager flagsManager;

    private String flagGroupName;

    protected MultiverseCommand(@NotNull MultiverseCore plugin) {
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();
        this.flagsManager = plugin.getMVCommandManager().getFlagsManager();
    }

    protected void registerFlagGroup(@NotNull CommandFlagGroup flagGroup) {
        if (flagGroupName != null) {
            throw new IllegalStateException("Flag group already registered! (name: " + flagGroupName + ")");
        }
        flagsManager.registerFlagGroup(flagGroup);
        flagGroupName = flagGroup.getName();
    }

    protected @NotNull ParsedCommandFlags parseFlags(@NotNull String[] flags) {
        return flagsManager.parse(flagGroupName, flags);
    }
}
