package com.onarandombox.MultiVerseCore.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiVerseCore.MVCommandHandler;
import com.onarandombox.MultiVerseCore.MultiVerseCore;

public class MVCreate extends MVCommandHandler {

    public MVCreate(MultiVerseCore plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sender.sendMessage("Not enough parameters to create a new world");
            sender.sendMessage(ChatColor.RED + "/mvcreate WORLDNAME ENVIRONMENT - Create a new World.");
            sender.sendMessage(ChatColor.RED + "Example - /mvcreate hellworld nether");
            return true;
        }
        if (new File(args[0].toString()).exists()) {
            sender.sendMessage(ChatColor.RED + "A Folder/World already exists with this name!");
            sender.sendMessage(ChatColor.RED + "If you are confident it is a world you can import with /mvimport");
            return true;
        }
        String name = args[0].toString();
        String env = args[1].toString();
        Environment environment = null;
        if (env.equalsIgnoreCase("NETHER"))
            environment = Environment.NETHER;

        if (env.equalsIgnoreCase("NORMAL"))
            environment = Environment.NORMAL;

        if (environment == null) {
            sender.sendMessage(ChatColor.RED
                    + "Environment type does not exist!");
            sender.sendMessage(ChatColor.RED
                    + "Only Normal & Nether exist as Environments");
            return false;
        }
        return false;
    }

}
