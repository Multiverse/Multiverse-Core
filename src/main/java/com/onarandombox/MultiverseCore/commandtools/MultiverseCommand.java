package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.BaseCommand;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagsManager;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import com.onarandombox.MultiverseCore.inject.AutoLoadedService;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.ContractsProvided;

@Contract
@ContractsProvided({BaseCommand.class})
public abstract class MultiverseCommand extends BaseCommand implements AutoLoadedService {

    protected final MVCommandManager commandManager;
    private String flagGroupName;

    protected MultiverseCommand(@NotNull MVCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    protected CommandFlagsManager getFlagsManager() {
        return commandManager.getFlagsManager();
    }

    protected void registerFlagGroup(@NotNull CommandFlagGroup flagGroup) {
        if (flagGroupName != null) {
            throw new IllegalStateException("Flag group already registered! (name: " + flagGroupName + ")");
        }
        getFlagsManager().registerFlagGroup(flagGroup);
        flagGroupName = flagGroup.getName();
    }

    protected @NotNull ParsedCommandFlags parseFlags(@NotNull String[] flags) {
        return getFlagsManager().parse(flagGroupName, flags);
    }
}
