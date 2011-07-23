package com.onarandombox.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class ExactDestination extends Destination {
    private MultiverseCore plugin;
    private MVWorld world;
    private final String coordRegex = "([\\d]+\\.?[\\d]*),([\\d]+\\.?[\\d]*),([\\d]+\\.?[\\d]*)";
    private String name;

    public ExactDestination(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifer() {
        return "e";
    }

    @Override
    public boolean isThisType(String destination) {
        List<String> parsed = Arrays.asList(destination.split(":"));
        // Need at least: e:world:x,y,z
        // OR e:world:x,y,z:pitch:yaw
        // so basically 3 or 5
        if (!(parsed.size() == 3 || parsed.size() == 5)) {
            return false;
        }
        // If it's not an Exact type
        if (!parsed.get(0).equalsIgnoreCase("e")) {
            return false;
        }
        parsed.remove(0);
        // If it's not a MV world
        if (!this.plugin.isMVWorld(parsed.get(0))) {
            return false;
        }
        parsed.remove(0);
        if (!parsed.get(0).matches(coordRegex)) {
            return false;
        }
        // This is 1 now, because we've removed 2
        if (parsed.size() == 1) {
            return true;
        }
        parsed.remove(0);
        try {
            Double.parseDouble(parsed.get(0));
        } catch (NumberFormatException e) {
            return false;
        }
        parsed.remove(0);
        try {
            Double.parseDouble(parsed.get(0));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setDestination(MultiverseCore plugin, String dest) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getType() {
        return "Portal";
    }

    @Override
    public String getName() {
        return this.name;
    }
}
