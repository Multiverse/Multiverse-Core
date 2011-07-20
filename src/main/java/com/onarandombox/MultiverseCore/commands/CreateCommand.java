package com.onarandombox.MultiverseCore.commands;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.CommandHandler;

public class CreateCommand extends MultiverseCommand {

    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Create World");
        this.setCommandUsage("/mvcreate" + ChatColor.GREEN + " {NAME} {ENV}" + ChatColor.GOLD + " -s [SEED] -g [GENERATOR[:ID]]");
        this.setArgRange(2, 6);
        this.addKey("mvcreate");
        this.addKey("mvc");
        this.addKey("mv create");
        this.setPermission("multiverse.core.create", "Creates a new world and loads it.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() % 2 != 0) {
            sender.sendMessage("You must preface your SEED with -s OR your GENERATOR with -g. Type /mv for help");
            return;
        }
        String worldName = args.get(0);
        String env = args.get(1);
        String seed = CommandHandler.getFlag("-s", args);
        String generator = CommandHandler.getFlag("-g", args);

        if (new File(worldName).exists() || this.plugin.isMVWorld(worldName)) {
            sender.sendMessage(ChatColor.RED + "A Folder/World already exists with this name!");
            sender.sendMessage(ChatColor.RED + "If you are confident it is a world you can import with /mvimport");
            return;
        }

        Environment environment = this.plugin.getEnvFromString(env);
        if (environment == null) {
            sender.sendMessage(ChatColor.RED + "That is not a valid environment.");
            EnvironmentCommand.showEnvironments(sender);
            return;
        }
        sender.sendMessage(ChatColor.AQUA + "Starting world creation...");
        if (this.plugin.addWorld(worldName, environment, seed, generator)) {
            sender.sendMessage(ChatColor.GREEN + "Complete!");
        } else {
            sender.sendMessage(ChatColor.RED + "FAILED.");
        }
        return;
    }
}
