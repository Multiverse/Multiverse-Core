/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.destination;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.commands.TeleportCommand;
import com.onarandombox.MultiverseCore.utils.PermissionTools;
import com.pneumaticraft.commandhandler.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.Map;
import java.util.zip.DataFormatException;

/** A factory class that will create destinations from specific strings. */
public class DestinationFactory {
    private MultiverseCore plugin;
    private Map<String, Class<? extends MVDestination>> destList;
    private Command teleportCommand;

    public DestinationFactory(MultiverseCore plugin) {
        this.plugin = plugin;
        this.destList = new HashMap<String, Class<? extends MVDestination>>();
        List<Command> cmds = this.plugin.getCommandHandler().getAllCommands();
        for (Command c : cmds) {
            if (c instanceof TeleportCommand) {
                this.teleportCommand = c;
            }
        }
    }

    /**
     * Gets a new destination from a string and possibly for a player.
     * Returns a new InvalidDestination if the string could not be parsed.
     *
     * @param destination The destination in string format.
     * @param player The player.
     *
     * @return A non-null MVDestination
     */
    public MVDestination getDestination(String destination, Player player) {
        String idenChar = "";
        if (destination.split(":").length > 1) {
            idenChar = destination.split(":")[0];
        }

        if (idenChar.equals("") && player != null && this.plugin.getMVWorldManager().isMVWorld(destination)) {
            // handle special case
            String playerID = player.getUniqueId().toString();
            File f = new File(this.plugin.getDataFolder(), MultiverseCore.PLAYER_LOCATION_DATA
                + File.separator + destination + File.separator + playerID + ".yaml");
            if (f.isFile()) {
                YamlConfiguration yc = new YamlConfiguration();
                try {
                    yc.load(f);
                } catch (Exception e) {
                    this.plugin.log(Level.SEVERE, "Failed to load saved location of player '"
                        + player.getName() + "' (" + playerID + ") in world '" + destination
                        + "': " + e.getMessage() + ".");
                    yc = null;
                    // fall through to use world spawn location
                }
                if (yc != null) {
                    try {
                        if (! yc.isSet("schema"))
                            throw new DataFormatException("missing schema node");
                        Object schema = yc.get("schema");
                        if (! Integer.class.isInstance(schema))
                            throw new DataFormatException("invalid schema version: "
                                + schema.toString());
                        if ((Integer) schema != 1)
                            throw new DataFormatException("invalid schema version: "
                                + schema.toString());

                        if (! yc.isSet("x"))
                            throw new DataFormatException("missing x location");
                        Object x = yc.get("x");
                        if (! Double.class.isInstance(x))
                            throw new DataFormatException("invalid data for x location: "
                                + x.toString());

                        if (! yc.isSet("y"))
                            throw new DataFormatException("missing y location");
                        Object y = yc.get("y");
                        if (! Double.class.isInstance(y))
                            throw new DataFormatException("invalid data for y location: "
                                + y.toString());

                        if (! yc.isSet("z"))
                            throw new DataFormatException("missing z location");
                        Object z = yc.get("z");
                        if (! Double.class.isInstance(z))
                            throw new DataFormatException("invalid data for z location: "
                                + z.toString());

                        if (! yc.isSet("yaw"))
                            throw new DataFormatException("missing yaw");
                        Object yaw = yc.get("yaw");
                        if (! Double.class.isInstance(yaw))
                            throw new DataFormatException("invalid data for yaw: "
                                + yaw.toString());

                        if (! yc.isSet("pitch"))
                            throw new DataFormatException("missing pitch");
                        Object pitch = yc.get("pitch");
                        if (! Double.class.isInstance(pitch))
                            throw new DataFormatException("invalid data for pitch: "
                                + pitch.toString());

                        MVDestination mydest = new ExactDestination();
                        ((ExactDestination) mydest).setDestination(new Location(
                            this.plugin.getMVWorldManager().getMVWorld(destination).getCBWorld(),
                            ((Double) x).doubleValue(), ((Double) y).doubleValue(), ((Double) z).doubleValue(),
                            ((Double) yaw).floatValue(), ((Double) pitch).floatValue()));
                        return mydest;

                    } catch (DataFormatException e) {
                        this.plugin.log(Level.SEVERE, "Failed to parse saved location of player '"
                            + player.getName() + "' (" + playerID + ") in world '" + destination
                            + "': " + e.getMessage() + ".");
                        // fall through to use world spawn location
                    }
                }
            } else {
                this.plugin.log(Level.FINE, "No saved location for player '" + player.getName()
                    + "' (" + playerID + ") in world '" + destination + "' found.");
            }
        }

        if (this.destList.containsKey(idenChar)) {
            Class<? extends MVDestination> myClass = this.destList.get(idenChar);
            try {
                MVDestination mydest = myClass.newInstance();
                if (!mydest.isThisType(this.plugin, destination)) {
                    return new InvalidDestination();
                }
                mydest.setDestination(this.plugin, destination);
                return mydest;
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return new InvalidDestination();
    }

    /**
     * Gets a new destination from a string.
     * Returns a new InvalidDestination if the string could not be parsed.
     *
     * @param destination The destination in string format.
     *
     * @return A non-null MVDestination
     */
    public MVDestination getDestination(String destination) {
        return getDestination(destination, null);
    }

    /**
     * Registers a {@link MVDestination}.
     *
     * @param c The {@link Class} of the {@link MVDestination} to register.
     * @param identifier The {@link String}-identifier.
     * @return True if the class was successfully registered.
     */
    public boolean registerDestinationType(Class<? extends MVDestination> c, String identifier) {
        if (this.destList.containsKey(identifier)) {
            return false;
        }
        this.destList.put(identifier, c);
        // Special case for world defaults:
        if (identifier.equals("")) {
            identifier = "w";
        }
        Permission self = this.plugin.getServer().getPluginManager().getPermission("multiverse.teleport.self." + identifier);
        Permission other = this.plugin.getServer().getPluginManager().getPermission("multiverse.teleport.other." + identifier);
        PermissionTools pt = new PermissionTools(this.plugin);
        if (self == null) {
            self = new Permission("multiverse.teleport.self." + identifier,
                    "Permission to teleport yourself for the " + identifier + " destination.", PermissionDefault.OP);
            this.plugin.getServer().getPluginManager().addPermission(self);
            pt.addToParentPerms("multiverse.teleport.self." + identifier);
        }
        if (other == null) {
            other = new Permission("multiverse.teleport.other." + identifier,
                    "Permission to teleport others for the " + identifier + " destination.", PermissionDefault.OP);
            this.plugin.getServer().getPluginManager().addPermission(other);
            pt.addToParentPerms("multiverse.teleport.other." + identifier);
        }
        this.teleportCommand.addAdditonalPermission(self);
        this.teleportCommand.addAdditonalPermission(other);
        return true;
    }
}
