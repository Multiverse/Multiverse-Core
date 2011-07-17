package com.onarandombox.MultiverseCore.commands;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class ReloadCommand extends MultiverseCommand {

    public ReloadCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Reload";
        this.commandDesc = "Reloads worlds.yml and config.yml.";
        this.commandUsage = "/mvreload";
        this.minimumArgLength = 0;
        this.maximumArgLength = 0;
        this.commandKeys.add("mvreload");
        this.commandKeys.add("mv reload");
        this.commandKeys.add("mvr");
        this.permission = "multiverse.reload";
        this.opRequired = true;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        this.plugin.log(Level.INFO, "Reloading Multiverse-Core config.yml and worlds.yml");
        this.plugin.loadConfigs();
        this.plugin.loadWorlds(true);
        this.plugin.log(Level.INFO, "Reload Complete!");
    }

}
