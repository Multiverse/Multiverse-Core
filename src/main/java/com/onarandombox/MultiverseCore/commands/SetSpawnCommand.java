/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * Sets the spawn for a world.
 */
public class SetSpawnCommand extends MultiverseCommand {
    public SetSpawnCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Set World Spawn");
        this.setCommandUsage("/mv setspawn [WORLD:X,Y,Z[:PITCH[:YAW]]]");
        this.setArgRange(0, 1);
        this.addKey("mvsetspawn");
        this.addKey("mvss");
        this.addKey("mv set spawn");
        this.addKey("mv setspawn");
        this.addKey("mvset spawn");
        this.addCommandExample("/mv set spawn");
        this.addCommandExample("/mv set spawn world:0,64,0");
        this.addCommandExample("/mv set spawn world:0,64,0:90");
        this.addCommandExample("/mv set spawn world:0,64,0:90:270");
        this.setPermission("multiverse.core.spawn.set", "Sets the spawn for the current world.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        setWorldSpawn(sender, args.size() > 0 ? args.get(0) : null);
    }

    /**
     * Does the actual spawn-setting-work.
     *
     * @param sender The {@link CommandSender} that's setting the spawn.
     * @param locationString The location that the spawn should be set to as a string in fhte format world:x,y,z[:pitch[:yaw]].
     *                       If the string is <tt>null</tt> if will use the sender's location if it is a player
     */
    protected void setWorldSpawn(CommandSender sender, String locationString) {
        Location l;
        if (locationString != null) {
            l = plugin.getLocationManipulation().stringToLocation(locationString);
            if (l == null) {
                sender.sendMessage(ChatColor.RED + locationString + ChatColor.WHITE + " is not a valid location! Location format is world:x,y,z[:pitch[:yaw]]");
                return;
            }
        } else if (sender instanceof Player) {
            l = ((Player) sender).getLocation();
        } else {
            sender.sendMessage("Append the spawn location in the format world:x,y,z[:pitch[:yaw]] to set the spawn from the console!");
            return;
        }

        World w = l.getWorld();
        MultiverseWorld foundWorld = this.plugin.getMVWorldManager().getMVWorld(w.getName());
        if (foundWorld != null) {
            foundWorld.setSpawnLocation(l);
            BlockSafety bs = this.plugin.getBlockSafety();
            if (!bs.playerCanSpawnHereSafely(l) && foundWorld.getAdjustSpawn()) {
                sender.sendMessage("It looks like that location would normally be unsafe. But I trust you.");
                sender.sendMessage("I'm turning off the Safe-T-Teleporter for spawns to this world.");
                sender.sendMessage("If you want this turned back on just do:");
                sender.sendMessage(ChatColor.AQUA + "/mvm set adjustspawn true " + foundWorld.getAlias());
                foundWorld.setAdjustSpawn(false);
            }
            sender.sendMessage("Spawn was set to: " + plugin.getLocationManipulation().strCoords(l));
            if (!plugin.saveWorldConfig()) {
                sender.sendMessage(ChatColor.RED + "There was an issue saving worlds.yml!  Your changes will only be temporary!");
            }
        } else {
            w.setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
            sender.sendMessage("Multiverse does not know about this world, only X,Y and Z set. Please import it to set the spawn fully (Pitch/Yaws).");
        }
    }
}
