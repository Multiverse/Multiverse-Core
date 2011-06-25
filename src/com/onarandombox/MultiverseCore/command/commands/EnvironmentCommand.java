package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class EnvironmentCommand extends BaseCommand{
    
    public EnvironmentCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "List Environments";
        this.description = "Lists valid known environments";
        this.usage = "/mvenv";
        this.minArgs = 0;
        this.maxArgs = 0;
        this.identifiers.add("mvenv");
        this.permission = "multiverse.world.list.environments";
        this.requiresOp = false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.showEnvironments(sender);
    }
    
    public void showEnvironments(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Valid Environments are:");
        sender.sendMessage(ChatColor.GREEN + "NORMAL");
        sender.sendMessage(ChatColor.RED + "NETHER");
        sender.sendMessage(ChatColor.AQUA + "SKYLANDS");
        if(this.plugin.getWorldGenerators().size() > 0) {
            sender.sendMessage(ChatColor.DARK_AQUA + "CUSTOM WORLD TYPES:");
        }
        for(String s : this.plugin.getWorldGenerators()) {
            sender.sendMessage(ChatColor.GOLD + s);
        }
    }
    
}
