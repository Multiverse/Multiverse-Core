/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * Takes the player to the latest bed he's slept in.
 */
public class SleepCommand extends MultiverseCommand {

    public SleepCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Go to Sleep");
        this.setCommandUsage("/mv sleep");
        this.setArgRange(0, 0);
        this.addKey("mv sleep");
        this.setPermission("multiverse.core.sleep", "Takes you the latest bed you've slept in (Currently BROKEN).", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        if (p == null) {
            return;
        }
    }
}
