/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.utils.LocationManipulation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class SetSpawnCommand extends MultiverseCommand {

    public SetSpawnCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Set World Spawn");
        this.setCommandUsage("/mv setspawn");
        this.setArgRange(0, 0);
        this.addKey("mvsetspawn");
        this.addKey("mvss");
        this.addKey("mv set spawn");
        this.addKey("mv setspawn");
        this.addKey("mvset spawn");
        this.setPermission("multiverse.core.spawn.set", "Sets the spawn for the current world.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location l = p.getLocation();
            World w = p.getWorld();
            MVWorld foundWorld = this.plugin.getWorldManager().getMVWorld(w.getName());
            if (foundWorld != null) {
                foundWorld.setSpawn(p.getLocation());
                sender.sendMessage("Spawn was set to: " + LocationManipulation.strCoords(p.getLocation()));
            } else {
                w.setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                sender.sendMessage("Multiverse does not know about this world, only X,Y and Z set. Please import it to set the spawn fully.");
            }

        } else {
            sender.sendMessage("You cannot use this command from the console.");
        }
    }
}
