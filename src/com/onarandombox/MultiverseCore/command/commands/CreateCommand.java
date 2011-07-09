package com.onarandombox.MultiverseCore.command.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;
import com.onarandombox.MultiverseCore.command.CommandManager;

public class CreateCommand extends BaseCommand {

    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Create World";
        this.description = "Creates a new world of the specified type";
        this.usage = "/mvcreate" + ChatColor.GREEN + " {NAME} {ENV}" + ChatColor.GOLD + " -s [SEED] -g [GENERATOR[:ID]]";
        this.minArgs = 2;
        this.maxArgs = 6;
        this.identifiers.add("mvcreate");
        this.permission = "multiverse.world.create";
        this.requiresOp = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length % 2 != 0) {
            sender.sendMessage("You must preface your SEED with -s OR your GENERATOR with -g. Type /mv for help");
            return;
        }
        String worldName = args[0];
        String env = args[1];
        String seed = CommandManager.getFlag("-s", args);
        String generator = CommandManager.getFlag("-g", args);

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
