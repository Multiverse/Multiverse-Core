package com.onarandombox.MultiverseCore;

import org.bukkit.command.CommandSender;

public abstract class MVCommandHandler {

    protected final MultiverseCore plugin;

    public MVCommandHandler(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    public abstract boolean perform(CommandSender sender, String[] args);
}
