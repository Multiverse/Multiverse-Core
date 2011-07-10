package com.onarandombox.MultiverseCore.command.commands;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class ReloadCommand extends Command {

    public ReloadCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Reload worlds.yml";
        this.commandDesc = "Reloads all worlds that are in worlds.yml. Use this if you've modified worlds.yml.";
        this.commandUsage = "/mvreload";
        this.minimumArgLength = 0;
        this.maximumArgLength = 0;
        this.commandKeys.add("mvreload");
        this.permission = "multiverse.world.reload";
        this.opRequired = true;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        ((MultiverseCore) this.plugin).log(Level.INFO, "Reloading Multiverse-Core config");
        ((MultiverseCore) this.plugin).loadConfigs();
        ((MultiverseCore) this.plugin).loadWorlds(true);
        ((MultiverseCore) this.plugin).log(Level.INFO, "Reload Complete!");
    }

}
