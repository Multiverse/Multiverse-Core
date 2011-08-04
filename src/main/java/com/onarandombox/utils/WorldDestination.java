package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class WorldDestination implements MVDestination {
    private boolean isValid;
    private MVWorld world;
    float yaw = -1;
    String direction = "";

    @Override
    public String getIdentifer() {
        return "w";
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        String[] items = destination.split(":");
        if (items.length > 3) {
            return false;
        }
        if (items.length == 1 && ((MultiverseCore) plugin).isMVWorld(items[0])) {
            // This case is: world
            return true;
        }
        if (items.length == 2 && ((MultiverseCore) plugin).isMVWorld(items[0])) {
            // This case is: world:n
            return true;
        } else if (items[0].equalsIgnoreCase("w") && ((MultiverseCore) plugin).isMVWorld(items[1])) {
            // This case is: w:world
            // and w:world:ne
            return true;
        }
        return false;
    }

    @Override
    public Location getLocation() {
        Location spawnLoc = this.world.getCBWorld().getSpawnLocation();
        if (this.yaw >= 0) {
            // Only modify the yaw if its set. 
            spawnLoc.setYaw(this.yaw);
            
        }
        spawnLoc.add(.5, 0, .5);
        return spawnLoc;
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public void setDestination(JavaPlugin plugin, String dest) {
        String[] items = dest.split(":");
        if (items.length > 3) {
            isValid = false;
            return;
        }
        if (items.length == 1 && ((MultiverseCore) plugin).isMVWorld(items[0])) {
            isValid = true;
            this.world = ((MultiverseCore) plugin).getMVWorld(items[0]);
            return;
        }
        if (items.length == 2 && ((MultiverseCore) plugin).isMVWorld(items[0])) {
            this.world = ((MultiverseCore) plugin).getMVWorld(items[0]);
            this.yaw = LocationManipulation.getYaw(items[1]);
            return;
        }
        if (items[0].equalsIgnoreCase("w") && ((MultiverseCore) plugin).isMVWorld(items[1])) {
            this.world = ((MultiverseCore) plugin).getMVWorld(items[1]);
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
        return this.world.getName();
    }

}
