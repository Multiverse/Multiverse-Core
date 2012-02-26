/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.destination.InvalidDestination;
import com.onarandombox.MultiverseCore.utils.MVPermissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;
/**
 * Checks to see if a player can go to a destination.
 */
public class CheckCommand extends MultiverseCommand {

    public CheckCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Help you validate your multiverse settings");
        this.setCommandUsage("/mv check " + ChatColor.GREEN + "{PLAYER} {DESTINATION}");
        this.setArgRange(2, 2);
        this.addKey("mv check");
        this.addKey("mvcheck");
        this.addCommandExample("/mv check " + ChatColor.GREEN + "fernferret " + ChatColor.LIGHT_PURPLE + "w:MyWorld");
        this.addCommandExample("/mv check " + ChatColor.GREEN + "Rigby90 " + ChatColor.LIGHT_PURPLE + "p:MyPortal");
        this.addCommandExample("/mv check " + ChatColor.GREEN + "lithium3141 " + ChatColor.LIGHT_PURPLE + "ow:WarpName");
        this.setPermission("multiverse.core.debug", "Checks to see if a player can go to a destination. Prints debug if false.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Player p = this.plugin.getServer().getPlayer(args.get(0));
        if (p == null) {
            sender.sendMessage("Could not find player " + ChatColor.GREEN + args.get(0));
            sender.sendMessage("Are they online?");
            return;
        }
        MVDestination dest = this.plugin.getDestFactory().getDestination(args.get(1));
        if (dest instanceof InvalidDestination) {
            sender.sendMessage(String.format("You asked if '%s' could go to %s%s%s,",
                    args.get(0), ChatColor.GREEN, args.get(0), ChatColor.WHITE));
            sender.sendMessage("but I couldn't find a Destination of that name? Did you type it correctly?");
            return;
        }

        MVPermissions perms = this.plugin.getMVPerms();
        perms.tellMeWhyICantDoThis(sender, p, dest);
    }
}
