/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public class AnchorManager {
    private MultiverseCore plugin;
    private Map<String, Location> anchors;
    private FileConfiguration anchorConfig;

    public AnchorManager(MultiverseCore plugin) {
        this.plugin = plugin;
        this.anchors = new HashMap<String, Location>();
    }

    public void loadAnchors() {
        this.anchors = new HashMap<String, Location>();
        this.anchorConfig = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "anchors.yml"));
        this.ensureConfigIsPrepared();
        ConfigurationSection anchors = this.anchorConfig.getConfigurationSection("anchors");
        Set<String> anchorKeys = anchors.getKeys(false);
        for (String key : anchorKeys) {
            //world:x,y,z:pitch:yaw
            Location anchorLocation = LocationManipulation.stringToLocation(anchors.getString(key, ""));
            if (anchorLocation != null) {
                MultiverseCore.staticLog(Level.INFO, "Loading anchor:  '" + key + "'...");
                this.anchors.put(key, anchorLocation);
            } else {
                MultiverseCore.staticLog(Level.WARNING, "The location for anchor '" + key + "' is INVALID.");
            }

        }
    }

    private void ensureConfigIsPrepared() {
        if (this.anchorConfig.getConfigurationSection("anchors") == null) {
            this.anchorConfig.createSection("anchors");
        }
    }

    public boolean saveAnchors() {
        try {
            this.anchorConfig.save(new File(this.plugin.getDataFolder(), "anchors.yml"));
            return true;
        } catch (IOException e) {
            this.plugin.log(Level.SEVERE, "Failed to save anchors.yml. Please check your file permissions.");
            return false;
        }
    }

    public Location getAnchorLocation(String anchor) {
        if (this.anchors.containsKey(anchor)) {
            return this.anchors.get(anchor);
        }
        return null;
    }

    public boolean saveAnchorLocation(String anchor, String location) {
        Location parsed = LocationManipulation.stringToLocation(location);
        return parsed != null && this.saveAnchorLocation(anchor, parsed);
    }

    public boolean saveAnchorLocation(String anchor, Location l) {
        if (l == null) {
            return false;
        }
        this.anchorConfig.set("anchors." + anchor, LocationManipulation.locationToString(l));
        this.anchors.put(anchor, l);
        return this.saveAnchors();
    }

    public Set<String> getAllAnchors() {
        return this.anchors.keySet();
    }

    public Set<String> getAnchors(Player p) {
        if (p == null) {
            return this.anchors.keySet();
        }
        Set<String> anchors = new HashSet<String>();
        for(String anchor : this.anchors.keySet()) {
            Location ancLoc = this.anchors.get(anchor);
            if(ancLoc == null) {
                continue;
            }
            if(p.hasPermission("multiverse.access." + ancLoc.getWorld().getName())) {
                anchors.add(anchor);
            }
        }
        return anchors;
    }

    public boolean deleteAnchor(String s) {
        if(this.anchors.containsKey(s)) {
            this.anchors.remove(s);
            this.anchorConfig.set("anchors." + s, null);
            return this.saveAnchors();
        }
        return false;
    }
}
