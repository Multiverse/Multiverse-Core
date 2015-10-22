/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;

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
        this.addKey("mvcl");
        this.addKey("mv cl");
        this.addKey("mv clone");
        this.addCommandExample("/mv clone " + ChatColor.GOLD + "world" + ChatColor.GREEN + " world_backup");
        this.addCommandExample("/mv clone " + ChatColor.GOLD + "skyblock_pristine" + ChatColor.GREEN + " skyblock");
        this.setPermission("multiverse.core.clone", "Clones a world.", PermissionDefault.OP);
        this.worldManager = this.plugin.getMVWorldManager();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Class<?>[] paramTypes = {String.class, String.class, String.class};
        List<Object> objectArgs = new ArrayList<Object>();
        objectArgs.add(args.get(0));
        objectArgs.add(args.get(1));
        if (!this.worldManager.isMVWorld(args.get(0))) {
            // If no world was found, we can't clone.
            sender.sendMessage("Sorry, Multiverse doesn't know about world " + args.get(0) + ", so we can't clone it!");
            sender.sendMessage("Check the " + ChatColor.GREEN + "/mv list" + ChatColor.WHITE + " command to verify it is listed.");
            return;
        }
        this.plugin.getCommandHandler().queueCommand(sender, "mvclone", "cloneWorld", objectArgs,
                paramTypes, ChatColor.GREEN + "World Cloned!", ChatColor.RED + "World could NOT be cloned!");
    }
}
