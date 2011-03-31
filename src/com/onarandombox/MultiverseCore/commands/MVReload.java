package com.onarandombox.MultiverseCore.commands;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MVCommandHandler;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class MVReload extends MVCommandHandler {

    public MVReload(MultiverseCore plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        // TODO: Implement Permissions
        plugin.log(Level.INFO, "Reloading Multiverse-Core config");
        plugin.loadConfigs();
        plugin.loadWorlds();
        plugin.log(Level.INFO, "Reload Complete!");
        return true;
    }

}
