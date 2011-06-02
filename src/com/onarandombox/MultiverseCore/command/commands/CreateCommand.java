package com.onarandombox.MultiverseCore.command.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

public class CreateCommand extends BaseCommand {

    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Create World";
        description = "Creates a new world of the specified type";
        usage = "/mvcoord" + ChatColor.GREEN + "{NAME} {TYPE}";
        minArgs = 2;
        maxArgs = 2;
        identifiers.add("mvcoord");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // TODO: Permissions, check
        if (args.length != 2) {
            sender.sendMessage("Not enough parameters to create a new world");
            sender.sendMessage(ChatColor.RED + "/mvcreate WORLDNAME ENVIRONMENT - Create a new World.");
            sender.sendMessage(ChatColor.RED + "Example - /mvcreate hellworld nether");
            return;
        }
        if (new File(args[0].toString()).exists()) {
            sender.sendMessage(ChatColor.RED + "A Folder/World already exists with this name!");
            sender.sendMessage(ChatColor.RED + "If you are confident it is a world you can import with /mvimport");
            return;
        }
        // String name = args[0].toString();
        String env = args[1].toString();
        Environment environment = null;
        if (env.equalsIgnoreCase("NETHER") || env.equalsIgnoreCase("HELL"))
            environment = Environment.NETHER;

        if (env.equalsIgnoreCase("NORMAL"))
            environment = Environment.NORMAL;
        
        if (env.equalsIgnoreCase("SKYLANDS") || env.equalsIgnoreCase("SKYLAND"))
            environment = Environment.SKYLANDS;

        if (environment == null) {
            sender.sendMessage(ChatColor.RED + "Environment type " + env + " does not exist!");
            return;
        }
        return;
    }

}
