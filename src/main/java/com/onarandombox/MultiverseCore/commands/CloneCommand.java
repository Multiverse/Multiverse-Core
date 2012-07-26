/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.exceptions.PropertyDoesNotExistException;
import com.onarandombox.MultiverseCore.utils.FileUtils;
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
import java.util.logging.Logger;

/**
 * Creates a clone of a world.
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
        this.addCommandExample("/mv clone " + ChatColor.GOLD + "CleanRoom" + ChatColor.GREEN + " CleanRoomCopy" + ChatColor.DARK_AQUA + " -g CleanRoomGenerator");
        this.setPermission("multiverse.core.clone", "Clones a world.", PermissionDefault.OP);
        this.worldManager = this.plugin.getMVWorldManager();
    }
    
    private void deleteUID(File worldFolder) throws IOException {
        File uidFile = new File(worldFolder, "uid.dat");
        uidFile.delete();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Class<?>[] paramTypes = {String.class, String.class, String.class};
        List<Object> objectArgs = new ArrayList<Object>();
        objectArgs.add(args.get(0));
        objectArgs.add(args.get(1));
        objectArgs.add(CommandHandler.getFlag("-g", args));
        this.plugin.getCommandHandler().queueCommand(sender, "mvclone", "cloneWorld", objectArgs,
                paramTypes, ChatColor.GREEN + "World Cloned!", ChatColor.RED + "World could NOT be cloned!");
    }
}
