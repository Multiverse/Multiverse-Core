package com.onarandombox.MultiverseCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class EnvironmentCommand extends Command {

    public EnvironmentCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "List Environments";
        this.commandDesc = "Lists valid known environments.";
        this.commandUsage = "/mvenv";
        this.minimumArgLength = 0;
        this.maximumArgLength = 0;
        this.commandKeys.add("mvenv");
        this.commandKeys.add("mv env");
        this.permission = "multiverse.world.list.environments";
        this.opRequired = false;
    }


    public static void showEnvironments(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Valid Environments are:");
        sender.sendMessage(ChatColor.GREEN + "NORMAL");
        sender.sendMessage(ChatColor.RED + "NETHER");
        sender.sendMessage(ChatColor.AQUA + "SKYLANDS");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        EnvironmentCommand.showEnvironments(sender);
    }
}
