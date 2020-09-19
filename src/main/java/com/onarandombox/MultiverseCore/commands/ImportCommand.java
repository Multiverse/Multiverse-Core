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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

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
    
    private String trimWorldName(String userInput) {
        // Removes relative paths.
        return userInput.replaceAll("^[./\\\\]+", "");
    }

    private void potentialWorldMessage(CommandSender sender) {
        List<String> potentialWorlds = this.worldManager.getPotentialWorlds();

        if (potentialWorlds.size() < 1) {
            sender.sendMessage(ChatColor.RED + "No potential worlds found. Sorry!");
        }

        StringBuilder worldList = new StringBuilder();
        ChatColor currColor = ChatColor.WHITE;

        for (String world : potentialWorlds) {
            worldList.append(currColor).append(world).append(" ");
            if (currColor == ChatColor.WHITE) {
                currColor = ChatColor.YELLOW;
            } else {
                currColor = ChatColor.WHITE;
            }
        }

        sender.sendMessage(ChatColor.AQUA + "====[ These look like worlds ]====");
        sender.sendMessage(worldList.toString());
    }

    private void failedMessage(CommandSender sender, String reason) {
        sender.sendMessage(ChatColor.RED + "FAILED.");
        sender.sendMessage(ChatColor.RED + reason);
    }

    private boolean checkValidWorld(CommandSender sender, String worldName) {
        File worldFile = new File(this.plugin.getServer().getWorldContainer(), worldName);

        if (this.worldManager.isMVWorld(worldName)) {
            sender.sendMessage(ChatColor.GREEN + "Multiverse" + ChatColor.WHITE
                    + " already knows about '" + ChatColor.AQUA + worldName + ChatColor.WHITE + "'!");
            return false;
        }
        if (!worldFile.exists()) {
            this.failedMessage(sender, "'" + worldName + "' folder does not exist. These look like worlds to me:");
            this.potentialWorldMessage(sender);
            return false;
        }
        if (!this.worldManager.isValidWorld(worldFile)) {
            this.failedMessage(sender, String.format("'%s' does not appear to be a world. It is lacking a level.dat file.", worldName));
            return false;
        }

        return true;
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = trimWorldName(args.get(0));

        // List down possible worlds available for import
        if (args.size() == 1 && (worldName.toLowerCase().equals("--list") || worldName.toLowerCase().equals("-l"))) {
            this.potentialWorldMessage(sender);
            return;
        }

        // Since we made an exception for the list, we have to make sure they have at least 2 params:
        // Note the exception is --list, which is covered above.
        if (args.size() == 1 || worldName.length() < 1) {
            this.showHelp(sender);
            return;
        }

        // Ensure world available for import
        if (!this.checkValidWorld(sender, worldName)) {
            return;
        }

        // Generator and SpawnAdjust
        String generator = CommandHandler.getFlag("-g", args);
        boolean useSpawnAdjust = true;
        for (String s : args) {
            if (s.equalsIgnoreCase("-n")) {
                useSpawnAdjust = false;
            }
        }

        String env = args.get(1);

        // Vanilla import
        if (env.toLowerCase().equals("vanilla")) {
            Command.broadcastCommandMessage(sender, "Splitting up vanilla world folder...");
            if (!this.worldManager.splitVanillaWorld(worldName)) {
                Command.broadcastCommandMessage(sender, ChatColor.RED + "Error splitting '" + worldName + "' folder to it's overworld, nether and end.");
                return;
            }
        }

        // Import all 3 default environments
        if (env.toLowerCase().equals("all") || env.toLowerCase().equals("vanilla")) {
            String netherName = worldName + "_nether";
            String endName = worldName + "_the_end";
            boolean allSuccess = true;

            if (!this.checkValidWorld(sender, netherName)) {
                return;
            }

            if (!this.checkValidWorld(sender, endName)) {
                return;
            }

            Command.broadcastCommandMessage(sender, String.format("Starting import of worlds '%s', '%s', '%s'...", worldName, netherName, endName));

            if (!this.worldManager.addWorld(worldName, Environment.NORMAL, null, null, null, generator, useSpawnAdjust)) {
                Command.broadcastCommandMessage(sender,  ChatColor.RED + "Error occurred while importing " + worldName);
                allSuccess = false;
            }
            if (!this.worldManager.addWorld(netherName, Environment.NETHER, null, null, null, generator, useSpawnAdjust)) {
                Command.broadcastCommandMessage(sender,  ChatColor.RED + "Error occurred while importing " + netherName);
                allSuccess = false;
            }
            if (!this.worldManager.addWorld(endName, Environment.THE_END, null, null, null, generator, useSpawnAdjust)) {
                Command.broadcastCommandMessage(sender,  ChatColor.RED + "Error occurred while importing " + endName);
                allSuccess = false;
            }

            if (allSuccess) {
                Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Completed!");
            }
            return;
        }

        // Environment
        Environment environment = EnvironmentCommand.getEnvFromString(env);
        if (environment == null) {
            this.failedMessage(sender, "That is not a valid environment.");
            EnvironmentCommand.showEnvironments(sender);
            return;
        }
        if (env == null) {
            this.failedMessage(sender, "You need to specify a world environment!");
            EnvironmentCommand.showEnvironments(sender);
            return;
        }

        // Import a world
        Command.broadcastCommandMessage(sender, String.format("Starting import of world '%s'...", worldName));
        if (this.worldManager.addWorld(worldName, environment, null, null, null, generator, useSpawnAdjust)) {
            Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Completed!");
        }
        else {
            Command.broadcastCommandMessage(sender, ChatColor.RED + "Error occurred while importing " + worldName);
        }
    }
}
