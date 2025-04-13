/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.utils.WorldNameChecker;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
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

    private String getPotentialWorldStrings() {
        final Collection<String> potentialWorlds = this.worldManager.getPotentialWorlds();
        StringBuilder worldList = new StringBuilder();
        ChatColor currColor = ChatColor.WHITE;

        for (String world : potentialWorlds) {
            worldList.append(currColor).append(world).append(' ');
            currColor = currColor == ChatColor.WHITE ? ChatColor.YELLOW : ChatColor.WHITE;
        }

        return worldList.toString();
    }
    
    private String trimWorldName(String userInput) {
        // Removes relative paths.
        return userInput.replaceAll("^[./\\\\]+", "");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = trimWorldName(args.get(0));

        if (worldName.toLowerCase().equals("--list") || worldName.toLowerCase().equals("-l")) {
            String worldList = this.getPotentialWorldStrings();
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
		
		// Make sure the world name doesn't contain the words 'plugins' and '.dat'
		if(worldName.contains("plugins")||worldName.contains(".dat")){
			sender.sendMessage(ChatColor.RED + "Multiverse cannot create a world that contains 'plugins' or '.dat'");
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
        //CustomGenerators start
        if(env.equalsIgnoreCase("VOID")){
            env = "NORMAL";
            generator = "multiverse:void";
        }
        //CustomGenerators end
        Environment environment = EnvironmentCommand.getEnvFromString(env);
        if (environment == null) {
            sender.sendMessage(ChatColor.RED + "That is not a valid environment.");
            EnvironmentCommand.showEnvironments(sender);
            return;
        }


        if (!worldFile.exists()) {
            sender.sendMessage(ChatColor.RED + "FAILED.");
            String worldList = this.getPotentialWorldStrings();
            sender.sendMessage("That world folder does not exist. These look like worlds to me:");
            sender.sendMessage(worldList);
        } else if (!WorldNameChecker.isValidWorldFolder(worldFile)) {
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