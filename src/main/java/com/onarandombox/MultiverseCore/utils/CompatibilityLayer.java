package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.lang.reflect.Method;

/**
 * Utility class to enable version specific minecraft features.
 */
public class CompatibilityLayer {

    private static Method checkAnchorSpawn;
    private static boolean useTravelAgent;
    private static Method playerPortalSearchRadius;
    private static Method entityPortalSearchRadius;

    /**
     * Initialise the reflection class, methods and fields.
     */
    public static void init() {
        checkAnchorSpawn = ReflectHelper.getMethod(PlayerRespawnEvent.class, "isAnchorSpawn");
        useTravelAgent = ReflectHelper.hasClass("org.bukkit.TravelAgent");
        playerPortalSearchRadius = ReflectHelper.getMethod(PlayerPortalEvent.class, "setSearchRadius", int.class);
        entityPortalSearchRadius = ReflectHelper.getMethod(EntityPortalEvent.class, "setSearchRadius", int.class);
    }

    /**
     * <p>Check if the respawn point is of respawn anchor type.</p>
     * <p>Introduced in minecraft 1.16</p>
     *
     * @param event A player respawn event.
     * @return If the respawn location is an anchor point.
     */
    public static boolean isAnchorSpawn(PlayerRespawnEvent event) {
        if (checkAnchorSpawn == null) {
            return false;
        }
        Boolean result = ReflectHelper.invokeMethod(event, checkAnchorSpawn);
        if (result == null) {
            Logging.warning("Unable to check if spawning at respawn anchor!");
            return false;
        }
        return result;
    }

    /**
     * <p>Gets if Travel Agent is supported on the server's minecraft version.</p>
     * <p>Removed in minecraft 1.14</p>
     *
     * @return True if Travel Agent is supported, else false.
     */
    public static boolean isUseTravelAgent() {
        return useTravelAgent;
    }

    /**
     * <p>Sets search radius for a PlayerPortalEvent.</p>
     *
     * <p>Use travel agent if available, else using new PlayerPortalEvent.setSearchRadius(int) method
     * introduced in minecraft 1.15</p>
     *
     * @param event         A Player Portal Event.
     * @param searchRadius  Target search radius to set to.
     */
    public static void setPortalSearchRadius(PlayerPortalEvent event, int searchRadius) {
        if (useTravelAgent) {
            event.getPortalTravelAgent().setSearchRadius(searchRadius);
            event.useTravelAgent(true);
            Logging.finer("Used travel agent to set player portal search radius.");
            return;
        }
        if (playerPortalSearchRadius == null) {
            Logging.warning("Unable to set player portal search radius!");
            return;
        }
        ReflectHelper.invokeMethod(event, playerPortalSearchRadius, searchRadius);
        Logging.finer("Used new method to set player portal search radius.");
    }

    /**
     * <p>Sets search radius for a EntityPortalEvent.</p>
     *
     * <p>Use travel agent if available, else using new EntityPortalEvent.setSearchRadius(int) method
     * introduced in minecraft 1.15</p>
     *
     * @param event         A Entity Portal Event.
     * @param searchRadius  Target search radius to set to.
     */
    public static void setPortalSearchRadius(EntityPortalEvent event, int searchRadius) {
        if (useTravelAgent) {
            event.getPortalTravelAgent().setSearchRadius(searchRadius);
            event.useTravelAgent(true);
            Logging.finer("Used travel agent to set entity portal search radius.");
            return;
        }
        if (entityPortalSearchRadius == null) {
            Logging.warning("Unable to set entity portal search radius!");
            return;
        }
        ReflectHelper.invokeMethod(event, entityPortalSearchRadius, searchRadius);
        Logging.finer("Used new method to set entity portal search radius.");
    }
}
