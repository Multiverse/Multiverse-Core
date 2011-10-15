/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.List;

public class ImportCommand extends MultiverseCommand {
    private WorldManager worldManager;

    public ImportCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Import World");
        this.setCommandUsage("/mv import" + ChatColor.GREEN + " {NAME} {ENV} " + ChatColor.GOLD + "[GENERATOR[:ID]]");
        this.setArgRange(2, 3);
        this.addKey("mvimport");
        this.addKey("mvim");
        this.addKey("mv import");
        this.addCommandExample("/mv import " + ChatColor.GOLD + "gargamel" + ChatColor.GREEN + " normal");
        this.addCommandExample("/mv import " + ChatColor.GOLD + "hell_world" + ChatColor.GREEN + " nether");
        this.addCommandExample("/mv import " + ChatColor.GOLD + "CleanRoom" + ChatColor.GREEN + " normal" + ChatColor.DARK_AQUA + " CleanRoomGenerator");
        this.setPermission("multiverse.core.import", "Imports a new world of the specified type.", PermissionDefault.OP);
        this.worldManager = this.plugin.getMVWorldManager();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = args.get(0);
        if (this.worldManager.isMVWorld(worldName) && new File(worldName).exists()) {
            sender.sendMessage(ChatColor.RED + "Multiverse already knows about this world!");
            return;
        }

        String generator = null;
        if (args.size() == 3) {
            generator = args.get(2);
        }

        String env = args.get(1);
        Environment environment = this.plugin.getEnvFromString(env);
        if (environment == null) {
            sender.sendMessage(ChatColor.RED + "That is not a valid environment.");
            EnvironmentCommand.showEnvironments(sender);
            return;
        }



        if (new File(this.plugin.getServerFolder(), worldName).exists() && env != null) {
            sender.sendMessage(ChatColor.AQUA + "Starting world import...");
            this.worldManager.addWorld(worldName, environment, null, generator);
            sender.sendMessage(ChatColor.GREEN + "Complete!");
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
