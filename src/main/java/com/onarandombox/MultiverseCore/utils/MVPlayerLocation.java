/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.destination.ExactDestination;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.logging.Level;
import java.util.zip.DataFormatException;

public class MVPlayerLocation {

    private static final String PLAYER_LOCATION_DATA = "player_location_data";

    private static MultiverseCore plugin = null;

    public static void init(MultiverseCore p) {
        plugin = p;
    }

    public static void savePlayerLocation(Player player, Location location, String action) {
        String world    = location.getWorld().getName();
        String playerID = player.getUniqueId().toString();

        if (plugin == null)
            return;

        plugin.log(Level.FINE, "Player '" + player.getName()
                + "' (" + playerID + ") was in world '" + world + "' at "
                + Double.toString(location.getX()) + ", "
                + Double.toString(location.getY()) + ", "
                + Double.toString(location.getZ()) + ", "
                + Double.toString(location.getYaw()) + ", "
                + Double.toString(location.getPitch()) + " before " + action + ".");

        YamlConfiguration yc = new YamlConfiguration();
        yc.set("schema", 1);
        yc.set("world", world);
        yc.set("player", playerID);
        yc.set("x", location.getX());
        yc.set("y", location.getY());
        yc.set("z", location.getZ());
        yc.set("yaw", location.getYaw());
        yc.set("pitch", location.getPitch());

        File dir = new File(plugin.getDataFolder(),
            PLAYER_LOCATION_DATA + File.separator + world);

        try {
            dir.mkdirs();
        } catch (Exception e) {
            plugin.log(Level.SEVERE, "Failed to create directory '"
                + dir.toString() + "': " + e.getMessage());
        }

        try {
            yc.save(new File(dir, playerID + ".yaml"));
        } catch (Exception e) {
            plugin.log(Level.SEVERE, "Failed to save location of player '"
                + player.getName() + "' in world '" + world + "': "
                + e.getMessage());
        }
    }

    public static MVDestination getPlayerLastLocation(Player player, String world) {
        String playerID = player.getUniqueId().toString();
        File   file;

        if (plugin == null)
            return null;

        file = new File(plugin.getDataFolder(), PLAYER_LOCATION_DATA
            + File.separator + world + File.separator + playerID + ".yaml");

        if (file.isFile()) {
            YamlConfiguration yc = new YamlConfiguration();
            try {
                yc.load(file);
            } catch (Exception e) {
                plugin.log(Level.SEVERE, "Failed to load saved location of player '"
                    + player.getName() + "' (" + playerID + ") in world '" + world
                    + "': " + e.getMessage() + ".");
                return null;
            }
            try {
                if (! yc.isSet("schema"))
                    throw new DataFormatException("missing schema node");
                if (! yc.isInt("schema"))
                    throw new DataFormatException("invalid schema version: "
                        + yc.get("schema").toString());
                if (yc.getInt("schema") != 1)
                    throw new DataFormatException("invalid schema version: "
                        + yc.get("schema").toString());

                if (! yc.isSet("x"))
                    throw new DataFormatException("missing x location");
                if (! yc.isDouble("x"))
                    throw new DataFormatException("invalid data for x location: "
                        + yc.get("x").toString());

                if (! yc.isSet("y"))
                    throw new DataFormatException("missing y location");
                if (! yc.isDouble("y"))
                    throw new DataFormatException("invalid data for y location: "
                        + yc.get("y").toString());

                if (! yc.isSet("z"))
                    throw new DataFormatException("missing z location");
                if (! yc.isDouble("z"))
                    throw new DataFormatException("invalid data for z location: "
                        + yc.get("z").toString());

                if (! yc.isSet("yaw"))
                    throw new DataFormatException("missing yaw");
                if (! yc.isDouble("yaw"))
                    throw new DataFormatException("invalid data for yaw: "
                        + yc.get("yaw").toString());

                if (! yc.isSet("pitch"))
                    throw new DataFormatException("missing pitch");
                if (! yc.isDouble("pitch"))
                    throw new DataFormatException("invalid data for pitch: "
                        + yc.get("pitch").toString());

                MVDestination mydest = new ExactDestination();
                ((ExactDestination) mydest).setDestination(new Location(
                    plugin.getMVWorldManager().getMVWorld(world).getCBWorld(),
                    yc.getDouble("x"), yc.getDouble("y"), yc.getDouble("z"),
                    (float) yc.getDouble("yaw"), (float) yc.getDouble("pitch")));
                return mydest;

            } catch (DataFormatException e) {
                plugin.log(Level.SEVERE, "Failed to parse saved location of player '"
                    + player.getName() + "' (" + playerID + ") in world '" + world
                    + "': " + e.getMessage() + ".");
                return null;
            }
        } else {
            plugin.log(Level.FINE, "No saved location for player '" + player.getName()
                + "' (" + playerID + ") in world '" + world + "' found.");
            return null;
        }
    }
}
