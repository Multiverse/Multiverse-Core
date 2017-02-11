/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.destination;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.utils.MVPlayerLocation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 * An exact {@link MVDestination}.
 */
public class LastLocationDestination implements MVDestination {
    private String direction = "";
    private boolean isSpawn;
    private Location location = null;
    private Player player = null;
    private MultiverseWorld world = null;
    private float yaw = 0;

    //  The internal state of an instance is in a superposition much like
    //  Schrödinger's cat.  Until observed via getLocation(Entity), the
    //  instance is both a player's last location in the world and the location
    //  of the spawn point in the world.  The superposition does not collapse
    //  into one or the other state until being observed via
    //  getLocation(Entity).  This is a result of how the MVDestination
    //  interface is defined.
    //  (Although, technially it is a superposition of not having the player's
    //  last location and not having the spawn location until being observed.)

    public static String getID() {
        return "l";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return this.getID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector getVelocity() {
        return new Vector(0, 0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        if (!(plugin instanceof MultiverseCore)) {
            return false;
        }

        // Need one of the following:
        //   world
        //   world:direction
        //   l:world
        //   l:world:direction
        String[] items = destination.split(":");
        if (items.length < 1 || items.length > 3) {
            return false;
        }
        if (items.length == 1) {
            if (((MultiverseCore) plugin).getMVWorldManager().isMVWorld(items[0])) {
                // world
                return true;
            }
        } else if (items.length == 2 && ((MultiverseCore) plugin).getMVWorldManager().isMVWorld(items[0])) {
            // world:direction
            return true;
        } else if (items[0].equalsIgnoreCase(this.getIdentifier()) && ((MultiverseCore) plugin).getMVWorldManager().isMVWorld(items[1])) {
            // l:world  OR  l:world:direction
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getLocation(Entity e) {
        if (!this.isValid() || e == null || !(e instanceof Player)) {
            return null;
        }

        player = (Player) e;
        this.location = MVPlayerLocation.getPlayerLastLocation(player, world);

        if (this.location == null) {
            this.location = world.getSpawnLocation();
            this.location.setYaw(this.yaw);
            this.isSpawn = true;
        } else {
            this.isSpawn = false;
        }

        return this.location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return this.world != null;
    }

    public boolean isSpawn() {
        return this.isSpawn;
    }

    public boolean isCollapsed() {
        return this.location != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDestination(JavaPlugin plugin, String destination) {
        this.direction = "";
        this.location = null;
        this.player = null;
        this.world = null;
        this.yaw = 0;

        if (!(plugin instanceof MultiverseCore)) {
            return;
        }

        // Need one of the following:
        //   world
        //   world:direction
        //   l:world
        //   l:world:direction
        String[] items = destination.split(":");
        if (items.length < 1 || items.length > 3) {
            return;
        }
        if (items.length == 1) {
            if (((MultiverseCore) plugin).getMVWorldManager().isMVWorld(items[0])) {
                // world
                this.world = ((MultiverseCore) plugin).getMVWorldManager().getMVWorld(items[0]);
                return;
            }
        } else if (items.length == 2 && ((MultiverseCore) plugin).getMVWorldManager().isMVWorld(items[0])) {
            // world:direction
            this.world = ((MultiverseCore) plugin).getMVWorldManager().getMVWorld(items[0]);
            this.direction = items[1];
            return;
        } else if (items[0].equalsIgnoreCase(this.getIdentifier()) && ((MultiverseCore) plugin).getMVWorldManager().isMVWorld(items[1])) {
            // l:world  OR  l:world:direction
            this.world = ((MultiverseCore) plugin).getMVWorldManager().getMVWorld(items[1]);
            if (items.length == 3) {
                this.direction = items[2];
                this.yaw = ((MultiverseCore) plugin).getLocationManipulation().getYaw(this.direction);
            }
            return;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "Last Location";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        if (this.isValid()) {
            return "Last Location ("
                + (player != null ? player.getName() + " in " : "")
                + this.world.getName() + ")";
        }
        return "Invalid Destination";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (this.isValid()) {
            return this.getIdentifier() + ":" + this.world.getName() + (this.direction.length() > 0 ?
                ":" + this.direction : "");
        }
        return "i:Invalid Destination";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequiredPermission() {
        return "multiverse.access." + this.world.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean useSafeTeleporter() {
        return true;
    }
}
