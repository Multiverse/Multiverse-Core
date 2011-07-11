package com.onarandombox.MultiverseCore.command.commands;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.Command;

public class ImportCommand extends Command {

    public ImportCommand(MultiverseCore plugin) {
        super(plugin);
        this.commandName = "Import World";
        this.commandDesc = "Imports a new world of the specified type";
        this.commandUsage = "/mvimport" + ChatColor.GREEN + " {NAME} {ENV} " + ChatColor.GOLD + "[GENERATOR[:ID]]";
        this.minimumArgLength = 2;
        this.maximumArgLength = 3;
        this.commandKeys.add("mvimport");
        this.commandKeys.add("mvi");
        this.commandKeys.add("mv import");
        this.permission = "multiverse.world.import";
        this.opRequired = true;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = args.get(0);
        if (((MultiverseCore) this.plugin).isMVWorld(worldName) && new File(worldName).exists()) {
            sender.sendMessage(ChatColor.RED + "Multiverse already knows about this world!");
            return;
        }

        String generator = null;
        if (args.size() == 3) {
            generator = args.get(2);
        }

        String env = args.get(1);
        Environment environment = ((MultiverseCore) this.plugin).getEnvFromString(env);
        if (environment == null) {
            sender.sendMessage(ChatColor.RED + "That is not a valid environment.");
            EnvironmentCommand.showEnvironments(sender);
            return;
        }

        if (new File(worldName).exists() && env != null) {
            sender.sendMessage(ChatColor.AQUA + "Starting world import...");
            ((MultiverseCore) this.plugin).addWorld(worldName, environment, null, generator);
            sender.sendMessage(ChatColor.GREEN + "Complete!");
            return;
        } else if (env == null) {
            sender.sendMessage(ChatColor.RED + "FAILED.");
            sender.sendMessage("That world type did not exist.");
            sender.sendMessage("For a list of available world types, type: /mvenv");
        } else {
            sender.sendMessage(ChatColor.RED + "FAILED.");
            sender.sendMessage("That world folder does not exist...");
        }
    }
}
