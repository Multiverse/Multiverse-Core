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
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Imports a new world of the specified type.
 */
public class CloneCommand extends MultiverseCommand {
    private MVWorldManager worldManager;

    public CloneCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Clone World");
        this.setCommandUsage("/mv clone" + ChatColor.GREEN + " {TARGET} {NAME}" + ChatColor.GOLD + " -g [GENERATOR[:ID]]");
        this.setArgRange(2, 4); // SUPPRESS CHECKSTYLE: MagicNumberCheck
        this.addKey("mvclone");
        this.addKey("mv clone");
        this.addCommandExample("/mv clone " + ChatColor.GOLD + "world" + ChatColor.GREEN + " world_backup");
        this.addCommandExample("/mv clone " + ChatColor.GOLD + "skyblock_pristine" + ChatColor.GREEN + " skyblock");
        this.addCommandExample("To clone a world that uses a generator, you'll need the generator:");
        this.addCommandExample("/mv clone " + ChatColor.GOLD + "CleanRoom" + ChatColor.GREEN + " normal" + ChatColor.DARK_AQUA + " -g CleanRoomGenerator");
        this.setPermission("multiverse.core.clone", "Clones a world.", PermissionDefault.OP);
        this.worldManager = this.plugin.getMVWorldManager();
    }
    
    private void copy(File from, File to) throws IOException {
    	// assumes from exists and to does not
    	to.mkdirs();
    	File[] files = from.listFiles();
    	for (File file : files) {
    		if (file.isDirectory()) {
    			copy(file, new File(to, file.getName()));
    		} else if (file.getName().equals("uid.dat")) {
    			// skip this because multiverse won't import a world with the same uid.dat as another one
    		} else {
    			File destFile = new File(to, file.getName());
    			destFile.createNewFile();
    			FileChannel src = new FileInputStream(file).getChannel();
    			FileChannel dest = new FileOutputStream(destFile).getChannel();
    			src.transferTo(0, src.size(), dest);
    			src.close();
    			dest.close();
    		}
    	}
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String oldWorldName = args.get(0);
        String newWorldName = args.get(1);

        // Make sure we don't already know about the new world.
        if (this.worldManager.isMVWorld(newWorldName)) {
            sender.sendMessage(ChatColor.GREEN + "Multiverse" + ChatColor.WHITE
                    + " already knows about '" + ChatColor.AQUA + newWorldName + ChatColor.WHITE + "'!");
            return;
        }
        
        // Make sure the old world is actually a world!
        if (this.worldManager.getUnloadedWorlds().contains(oldWorldName)) {
            sender.sendMessage("That world exists, but it is unloaded!");
            sender.sendMessage(String.format("You can load it with: %s/mv load %s", ChatColor.AQUA, oldWorldName));
            return;
        } else if (!this.worldManager.isMVWorld(oldWorldName)) {
            sender.sendMessage("You must enter a" + ChatColor.LIGHT_PURPLE + " world" + ChatColor.WHITE + " to clone!");
            return;
        }

        File oldWorldFile = new File(this.plugin.getServer().getWorldContainer(), oldWorldName);
        
        File newWorldFile = new File(this.plugin.getServer().getWorldContainer(), newWorldName);
        
        // Make sure the new world doesn't exist outside of multiverse.
        if (newWorldFile.exists()) {
        	sender.sendMessage(String.format("Something called '%s' already exists.", newWorldName));
        	return;
        }
        
        Command.broadcastCommandMessage(sender, String.format("Copying data for world '%s'...", oldWorldName));
        try {
        	copy(oldWorldFile, newWorldFile);
        } catch (IOException e) {
            Command.broadcastCommandMessage(sender, ChatColor.RED + "Failed!");
            e.printStackTrace();
            return;
        }
        
        WorldCreator worldCreator = new WorldCreator(newWorldName);
        worldCreator.copy(this.worldManager.getMVWorld(oldWorldName).getCBWorld());

        String generator = CommandHandler.getFlag("-g", args);
        boolean useSpawnAdjust = this.worldManager.getMVWorld(oldWorldName).getAdjustSpawn();

        Environment environment = worldCreator.environment();
        Command.broadcastCommandMessage(sender, String.format("Copying environment '%s'...", environment.toString()));

        if (newWorldFile.exists()) {
            Command.broadcastCommandMessage(sender, String.format("Starting import of world '%s'...", newWorldName));
            if (this.worldManager.addWorld(newWorldName, environment, null, null, null, generator, useSpawnAdjust))
                Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Complete!");
            else
                Command.broadcastCommandMessage(sender, ChatColor.RED + "Failed!");
        } else {
            sender.sendMessage(ChatColor.RED + "FAILED.");
            sender.sendMessage("Couldn't copy the world files.  Go complain to somebody.");
        }
    }
}
