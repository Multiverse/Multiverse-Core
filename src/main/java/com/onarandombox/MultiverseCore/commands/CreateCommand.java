/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.List;

/**
 * Creates a new world and loads it.
 */
public class CreateCommand extends MultiverseCommand {
    private MVWorldManager worldManager;

    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Create World");
        this.setCommandUsage("/mv create" + ChatColor.GREEN + " {NAME} {ENV}" + ChatColor.GOLD + " -s [SEED] -g [GENERATOR[:ID]] -t [WORLDTYPE] [-n] -a [true|false]");
        this.setArgRange(2, 11); // SUPPRESS CHECKSTYLE: MagicNumberCheck
        this.addKey("mvcreate");
        this.addKey("mvc");
        this.addKey("mv create");
        this.setPermission("multiverse.core.create", "Creates a new world and loads it.", PermissionDefault.OP);
        this.addCommandExample("/mv create " + ChatColor.GOLD + "world" + ChatColor.GREEN + " normal");
        this.addCommandExample("/mv create " + ChatColor.GOLD + "lavaland" + ChatColor.RED + " nether");
        this.addCommandExample("/mv create " + ChatColor.GOLD + "starwars" + ChatColor.AQUA + " end");
        this.addCommandExample("/mv create " + ChatColor.GOLD + "flatroom" + ChatColor.GREEN + " normal" + ChatColor.AQUA + " -t flat");
        this.addCommandExample("/mv create " + ChatColor.GOLD + "gargamel" + ChatColor.GREEN + " normal" + ChatColor.DARK_AQUA + " -s gargamel");
        this.addCommandExample("/mv create " + ChatColor.GOLD + "moonworld" + ChatColor.GREEN + " normal" + ChatColor.DARK_AQUA + " -g BukkitFullOfMoon");
        this.worldManager = this.plugin.getMVWorldManager();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = args.get(0);
        File worldFile = new File(this.plugin.getServer().getWorldContainer(), worldName);
        String env = args.get(1);
        String seed = CommandHandler.getFlag("-s", args);
        String generator = CommandHandler.getFlag("-g", args);
        boolean allowStructures = true;
        String structureString = CommandHandler.getFlag("-a", args);
        if (structureString != null) {
            allowStructures = Boolean.parseBoolean(structureString);
        }
        String typeString = CommandHandler.getFlag("-t", args);
        boolean useSpawnAdjust = true;
        for (String s : args) {
            if (s.equalsIgnoreCase("-n")) {
                useSpawnAdjust = false;
            }
        }

        if (worldFile.exists() || this.worldManager.isMVWorld(worldName)) {
            sender.sendMessage(ChatColor.RED + "A Folder/World already exists with this name!");
            sender.sendMessage(ChatColor.RED + "If you are confident it is a world you can import with /mvimport");
            return;
        }

        Environment environment = EnvironmentCommand.getEnvFromString(env);
        if (environment == null) {
            sender.sendMessage(ChatColor.RED + "That is not a valid environment.");
            EnvironmentCommand.showEnvironments(sender);
            return;
        }

        // If they didn't specify a type, default to NORMAL
        if (typeString == null) {
            typeString = "NORMAL";
        }
        WorldType type = EnvironmentCommand.getWorldTypeFromString(typeString);
        if (type == null) {
            sender.sendMessage(ChatColor.RED + "That is not a valid World Type.");
            EnvironmentCommand.showWorldTypes(sender);
            return;
        }

        Command.broadcastCommandMessage(sender, "Starting creation of world '" + worldName + "'...");

        if (this.worldManager.addWorld(worldName, environment, seed, type, allowStructures, generator, useSpawnAdjust)) {
            Command.broadcastCommandMessage(sender, "Complete!");
        } else {
            Command.broadcastCommandMessage(sender, "FAILED.");
        }
    }
}
