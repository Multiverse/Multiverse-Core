package com.onarandombox.MultiverseCore.plugins;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.plugins.destination.DynmapMarkerDestination;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapCore;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerSet;

import java.util.List;

/**
 * Non-Intrusive Connector class.
 */
public class DynmapConnector {

    private DynmapCore plugin;
    private MultiverseCore core;

    /**
     * This class should only ever get initialized ONCE!
     */
    public DynmapConnector(Plugin dynmapCore, MultiverseCore core) {
        this.plugin = (DynmapCore) dynmapCore;
        this.core = core;
        this.addDestinationToManager();
    }

    private void addDestinationToManager() {
        this.core.getDestFactory().registerDestinationType(DynmapMarkerDestination.class, "dm");
    }

    public Location getLocation(String markerSet, String marker) {
        MarkerSet set = this.plugin.getMarkerAPI().getMarkerSet(markerSet);
        if (set == null) {
            return null;
        }
        // Try to find it by the ID
        Marker m = set.findMarker(marker);
        if (m != null) {
            return this.getLocationFromMarker(m);
        }
        // Try to find it by the label
        m = set.findMarkerByLabel(marker);
        if (m != null) {
            return this.getLocationFromMarker(m);
        }
        return null;
    }

    private Location getLocationFromMarker(Marker m) {
        return new Location(this.core.getServer().getWorld(m.getWorld()), m.getX(), m.getY(), m.getZ());
    }
}
