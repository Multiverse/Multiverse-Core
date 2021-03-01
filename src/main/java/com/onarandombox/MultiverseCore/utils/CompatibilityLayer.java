package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.lang.reflect.Method;

/**
 * A utility class to enable version specific minecraft features.
 */
public class CompatibilityLayer {

    private static Method checkAnchorSpawn;

    /**
     * Initialise the reflection methods.
     */
    public static void init() {
        try {
            checkAnchorSpawn = PlayerRespawnEvent.class.getDeclaredMethod("isAnchorSpawn");
        } catch (NoSuchMethodException e) {
            Logging.fine("%s does not support respawn anchors.", Bukkit.getVersion());
        }
    }

    /**
     * Check if the respawn point is of respawn anchor type.
     * Introduced in minecraft 1.16
     *
     * @param event A player respawn event.
     * @return If the respawn location is an anchor point.
     */
    public static boolean isAnchorSpawn(PlayerRespawnEvent event) {
        if (checkAnchorSpawn == null) {
            return false;
        }
        try {
            return (boolean) checkAnchorSpawn.invoke(event);
        } catch (Exception e) {
            Logging.warning("Error checking for: %s", checkAnchorSpawn);
            e.printStackTrace();
        }
        return false;
    }
}