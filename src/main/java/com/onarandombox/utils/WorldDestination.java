package com.onarandombox.utils;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class WorldDestination extends Destination {
    private boolean isValid;
    private MVWorld world;

    @Override
    public String getIdentifer() {
        return "w";
    }

    @Override
    public boolean isThisType(JavaPlugin plugin, String destination) {
        String[] items = destination.split(":");
        if (items.length > 2) {
            return false;
        }
        if (items.length == 1 && ((MultiverseCore) plugin).isMVWorld(items[0])) {
            return true;
        }
        if (items[0].equalsIgnoreCase("w") && ((MultiverseCore) plugin).isMVWorld(items[1])) {
            return true;
        }
        return false;
    }

    @Override
    public Location getLocation() {
        return this.world.getCBWorld().getSpawnLocation();
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public void setDestination(JavaPlugin plugin, String dest) {
        String[] items = dest.split(":");
        if (items.length > 2) {
            isValid = false;
            return;
        }
        if(items.length == 1 && ((MultiverseCore) plugin).isMVWorld(items[0])) {
            isValid = true;
            this.world = ((MultiverseCore) plugin).getMVWorld(items[0]);
            return;
        }
        if (items[0].equalsIgnoreCase("w") && ((MultiverseCore) plugin).isMVWorld(items[1])) {
            this.world = ((MultiverseCore) plugin).getMVWorld(items[1]);
            isValid = true;
            return;
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
        return this.world.getCBWorld().getName();
    }

}
