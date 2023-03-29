package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.OpenBukkitCommandIssuer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MVCommandIssuer extends OpenBukkitCommandIssuer {

    private final MVCommandManager commandManager;

    MVCommandIssuer(@NotNull MVCommandManager commandManager, @NotNull CommandSender sender) {
        super(commandManager, sender);
        this.commandManager = commandManager;
    }

    @Override
    public MVCommandManager getManager() {
        return commandManager;
    }
}
