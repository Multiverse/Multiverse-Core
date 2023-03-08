package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.BaseCommand;
import com.onarandombox.MultiverseCore.api.MVPlugin;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagsManager;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;

@Contract
public abstract class MultiverseCommand extends BaseCommand {
    protected final CommandFlagsManager flagsManager;
    private String flagGroupName;

    protected MultiverseCommand(@NotNull MVPlugin plugin) {
        this.flagsManager = plugin.getCore().getMVCommandManager().getFlagsManager();
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
