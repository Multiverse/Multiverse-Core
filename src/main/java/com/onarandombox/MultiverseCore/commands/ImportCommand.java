/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class ImportCommand extends MultiverseCommand {
    private MVWorldManager worldManager;

    public ImportCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Import World");
        this.setCommandUsage("/mv import" + ChatColor.GREEN + " {NAME} {ENV} " + ChatColor.GOLD + "[GENERATOR[:ID]]");
        this.setArgRange(1, 3);
        this.addKey("mvimport");
        this.addKey("mvim");
        this.addKey("mv import");
        this.addCommandExample("/mv import " + ChatColor.GOLD + "gargamel" + ChatColor.GREEN + " normal");
        this.addCommandExample("/mv import " + ChatColor.GOLD + "hell_world" + ChatColor.GREEN + " nether");
        this.addCommandExample("/mv import " + ChatColor.GOLD + "CleanRoom" + ChatColor.GREEN + " normal" + ChatColor.DARK_AQUA + " CleanRoomGenerator");
        this.setPermission("multiverse.core.import", "Imports a new world of the specified type.", PermissionDefault.OP);
        this.worldManager = this.plugin.getMVWorldManager();
    }

    /**
     * A very basic check to see if a folder has a level.dat file.
     * If it does, we can safely assume it's a world folder.
     *
     * @param worldFolder The File that may be a world.
     * @return True if it looks like a world, false if not.
     */
    private boolean checkIfIsWorld(File worldFolder) {
        if (worldFolder.isDirectory()) {
            File[] files = worldFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    return name.equalsIgnoreCase("level.dat");
                }
            });
            if (files.length > 0) {
                return true;
            }
        }
        return false;
    }

    private String getPotentialWorlds() {
        File worldFolder = this.plugin.getServer().getWorldContainer();
        File[] files = worldFolder.listFiles();
        String worldList = "";
        ChatColor currColor = ChatColor.WHITE;
        for (File file : files) {
            if (file.isDirectory() && checkIfIsWorld(file)) {
                worldList += currColor + file.getName() + " ";
                if (currColor == ChatColor.WHITE) {
                    currColor = ChatColor.YELLOW;
                } else {
                    currColor = ChatColor.WHITE;
                }
            }
        }
        return worldList;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = args.get(0);

        if (worldName.toLowerCase().equals("--list") || worldName.toLowerCase().equals("-l")) {
            String worldList = this.getPotentialWorlds();
            sender.sendMessage(worldList);
            return;
        }
        // Since we made an exception for the list, we have to make sure they have at least 2 params:
        if (args.size() == 1) {
            this.showHelp(sender);
            return;
        }
        File worldFile = new File(this.plugin.getServerFolder(), worldName);
        if (this.worldManager.isMVWorld(worldName) && worldFile.exists()) {
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

        if (worldFile.exists() && env != null) {
            Command.broadcastCommandMessage(sender, "Starting import of world '" + worldName + "'...");
            this.worldManager.addWorld(worldName, environment, null, generator);
            Command.broadcastCommandMessage(sender, "Complete!");
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
