/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Imports a new world of the specified type.
 */
public class ImportCommand extends MultiverseCommand {
    private MVWorldManager worldManager;

    public ImportCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Import World");
        this.setCommandUsage("/mv import" + ChatColor.GREEN + " {NAME} {ENV}" + ChatColor.GOLD + " -g [GENERATOR[:ID]] [-n]");
        this.setArgRange(1, 5); // SUPPRESS CHECKSTYLE: MagicNumberCheck
        this.addKey("mvimport");
        this.addKey("mvim");
        this.addKey("mv import");
        this.addCommandExample("/mv import " + ChatColor.GOLD + "gargamel" + ChatColor.GREEN + " normal");
        this.addCommandExample("/mv import " + ChatColor.GOLD + "hell_world" + ChatColor.GREEN + " nether");
        this.addCommandExample("To import a world that uses a generator, you'll need the generator:");
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
    private static boolean checkIfIsWorld(File worldFolder) {
        if (worldFolder.isDirectory()) {
            File[] files = worldFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    return name.toLowerCase().endsWith(".dat");
                }
            });
            if (files != null && files.length > 0) {
                return true;
            }
        }
        return false;
    }

    private String getPotentialWorlds() {
        File worldFolder = this.plugin.getServer().getWorldContainer();
        if (worldFolder == null) {
            return "";
        }
        File[] files = worldFolder.listFiles();
        String worldList = "";
        Collection<MultiverseWorld> worlds = this.worldManager.getMVWorlds();
        List<String> worldStrings = new ArrayList<String>();
        for (MultiverseWorld world : worlds) {
            worldStrings.add(world.getName());
        }
        for (String world : this.worldManager.getUnloadedWorlds()) {
            worldStrings.add(world);
        }
        ChatColor currColor = ChatColor.WHITE;
        for (File file : files) {
            if (file.isDirectory() && checkIfIsWorld(file) && !worldStrings.contains(file.getName())) {
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
    
    private String trimWorldName(String userInput) {
        // Removes relative paths.
        return userInput.replaceAll("^[./\\\\]+", "");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = trimWorldName(args.get(0));

        if (worldName.toLowerCase().equals("--list") || worldName.toLowerCase().equals("-l")) {
            String worldList = this.getPotentialWorlds();
            if (worldList.length() > 2) {
                sender.sendMessage(ChatColor.AQUA + "====[ These look like worlds ]====");
                sender.sendMessage(worldList);
            } else {
                sender.sendMessage(ChatColor.RED + "No potential worlds found. Sorry!");
            }
            return;
        }
        // Since we made an exception for the list, we have to make sure they have at least 2 params:
        // Note the exception is --list, which is covered above.
        if (args.size() == 1 || worldName.length() < 1) {
            this.showHelp(sender);
            return;
        }

        // Make sure we don't already know about this world.
        if (this.worldManager.isMVWorld(worldName)) {
            sender.sendMessage(ChatColor.GREEN + "Multiverse" + ChatColor.WHITE
                    + " already knows about '" + ChatColor.AQUA + worldName + ChatColor.WHITE + "'!");
            return;
        }

        File worldFile = new File(this.plugin.getServer().getWorldContainer(), worldName);

        String generator = CommandHandler.getFlag("-g", args);
        boolean useSpawnAdjust = true;
        for (String s : args) {
            if (s.equalsIgnoreCase("-n")) {
                useSpawnAdjust = false;
            }
        }

        String env = args.get(1);
        Environment environment = EnvironmentCommand.getEnvFromString(env);
        if (environment == null) {
            sender.sendMessage(ChatColor.RED + "That is not a valid environment.");
            EnvironmentCommand.showEnvironments(sender);
            return;
        }

        if (!worldFile.exists()) {
            sender.sendMessage(ChatColor.RED + "FAILED.");
            String worldList = this.getPotentialWorlds();
            sender.sendMessage("That world folder does not exist. These look like worlds to me:");
            sender.sendMessage(worldList);
        } else if (!checkIfIsWorld(worldFile)) {
            sender.sendMessage(ChatColor.RED + "FAILED.");
            sender.sendMessage(String.format("'%s' does not appear to be a world. It is lacking a .dat file.",
                                             worldName));
        } else if (env == null) {
            sender.sendMessage(ChatColor.RED + "FAILED.");
            sender.sendMessage("That world environment did not exist.");
            sender.sendMessage("For a list of available world types, type: " + ChatColor.AQUA + "/mvenv");
        } else {
            Command.broadcastCommandMessage(sender, String.format("Starting import of world '%s'...", worldName));
            if (this.worldManager.addWorld(worldName, environment, null, null, null, generator, useSpawnAdjust))
                Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Complete!");
            else
                Command.broadcastCommandMessage(sender, ChatColor.RED + "Failed!");
        }
    }
}
