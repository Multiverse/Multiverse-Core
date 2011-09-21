/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class WorldDestination implements MVDestination {
    private boolean isValid;
    private MVWorld world;
    float yaw = -1;
    String direction = "";

    @Override
    public String getIdentifier() {
        return "w";
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        String[] items = destination.split(":");
        if (items.length > 3) {
            return false;
        }
        if (items.length == 1 && ((MultiverseCore) plugin).getWorldManager().isMVWorld(items[0])) {
            // This case is: world
            return true;
        }
        if (items.length == 2 && ((MultiverseCore) plugin).getWorldManager().isMVWorld(items[0])) {
            // This case is: world:n
            return true;
        } else if (items[0].equalsIgnoreCase("w") && ((MultiverseCore) plugin).getWorldManager().isMVWorld(items[1])) {
            // This case is: w:world
            // and w:world:ne
            return true;
        }
        return false;
    }

    @Override
    public Location getLocation(Entity e) {
        Location spawnLoc = getAcurateSpawnLocation(e, this.world);
        if (this.yaw >= 0) {
            // Only modify the yaw if its set.
            spawnLoc.setYaw(this.yaw);
        }
        return spawnLoc;
    }

    private Location getAcurateSpawnLocation(Entity e, MVWorld world) {
        if(world != null) {
            return world.getSpawnLocation();
        } else {
            return e.getWorld().getSpawnLocation().add(.5, 0, .5);
        }
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public void setDestination(JavaPlugin plugin, String destination) {
        String[] items = destination.split(":");
        if (items.length > 3) {
            isValid = false;
            return;
        }
        if (items.length == 1 && ((MultiverseCore) plugin).getWorldManager().isMVWorld(items[0])) {
            isValid = true;
            this.world = ((MultiverseCore) plugin).getWorldManager().getMVWorld(items[0]);
            return;
        }
        if (items.length == 2 && ((MultiverseCore) plugin).getWorldManager().isMVWorld(items[0])) {
            this.world = ((MultiverseCore) plugin).getWorldManager().getMVWorld(items[0]);
            this.yaw = LocationManipulation.getYaw(items[1]);
            return;
        }
        if (items[0].equalsIgnoreCase("w") && ((MultiverseCore) plugin).getWorldManager().isMVWorld(items[1])) {
            this.world = ((MultiverseCore) plugin).getWorldManager().getMVWorld(items[1]);
            isValid = true;
            if (items.length == 3) {
                this.yaw = LocationManipulation.getYaw(items[2]);
            }
        }
    }

    @Override
    public String getType() {
        return "World";
    }

    @Override
    public String getName() {
        return this.world.getColoredWorldString();
    }

    @Override
    public String toString() {
        if(direction.length() > 0 && yaw >= 0) {
            return this.world.getCBWorld().getName() + ":"+this.direction;
        }
        return this.world.getCBWorld().getName();
    }

    @Override
    public String getRequiredPermission() {
        return "multiverse.access."+this.world.getName();
    }
    public Vector getVelocity() {
        return new Vector(0,0,0);
    }

    @Override
    public boolean useSafeTeleporter() {
        return true;
    }

}
