package com.onarandombox.MultiverseCore.command.commands;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MVCommandHandler;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Reload worlds.yml";
        this.description = "Reloads all worlds that are in worlds.yml. Use this if you've modified worlds.yml.";
        this.usage = "/mvreload";
        this.minArgs = 0;
        this.maxArgs = 0;
        this.identifiers.add("mvreload");
        this.permission = "multiverse.world.reload";
        this.requiresOp = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.log(Level.INFO, "Reloading Multiverse-Core config");
        plugin.loadConfigs();
        plugin.loadWorlds();
        plugin.log(Level.INFO, "Reload Complete!");
    }

}
