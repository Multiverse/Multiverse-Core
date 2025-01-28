/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.anchor;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;

/**
 * Manages anchors.
 */
@Service
public final class AnchorManager {

    private static final String ANCHORS_FILE = "anchors.yml";
    private static final String ANCHORS_CONFIG_SECTION = "anchors";

    private Map<String, Location> anchors;
    private FileConfiguration anchorConfig;

    private final Plugin plugin;
    private final LocationManipulation locationManipulation;
    private final MVCoreConfig config;

    @Inject
    AnchorManager(
            MultiverseCore plugin,
            LocationManipulation locationManipulation,
            MVCoreConfig config) {
        this.plugin = plugin;
        this.locationManipulation = locationManipulation;
        this.config = config;

        anchors = new HashMap<>();
    }

    /**
     * Loads all anchors.
     */
    public void loadAnchors() {
        anchors = new HashMap<>();
        anchorConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), ANCHORS_FILE));
        var anchorsSection = getAnchorsConfigSection();
        Set<String> anchorKeys = anchorsSection.getKeys(false);
        for (String key : anchorKeys) {
            //world:x,y,z:pitch:yaw
            Location anchorLocation = locationManipulation.stringToLocation(anchorsSection.getString(key, ""));
            if (anchorLocation != null) {
                Logging.config("Loading anchor:  '%s'...", key);
                anchors.put(key, anchorLocation);
            } else {
                Logging.warning("The location for anchor '%s' is INVALID.", key);
            }

        }
    }

    private ConfigurationSection getAnchorsConfigSection() {
        var anchorsConfigSection = anchorConfig.getConfigurationSection(ANCHORS_CONFIG_SECTION);
        if (anchorsConfigSection == null) {
            anchorsConfigSection = anchorConfig.createSection(ANCHORS_CONFIG_SECTION);
        }
        return anchorsConfigSection;
    }

    /**
     * Saves all anchors.
     *
     * @return True if all anchors were successfully saved.
     */
    public Try<Void> saveAnchors() {
        return Try.run(() -> anchorConfig.save(new File(plugin.getDataFolder(), ANCHORS_FILE)))
                .onFailure(failure -> {
                    Logging.severe("Failed to save anchors.yml. Please check your file permissions.");
                });
    }

    /**
     * Gets the {@link Location} associated with an anchor.
     *
     * @param anchor The name of the anchor.
     * @return The {@link Location}.
     */
    public Location getAnchorLocation(String anchor) {
        if (anchors.containsKey(anchor)) {
            return anchors.get(anchor);
        }
        return null;
    }

    /**
     * Saves an anchor.
     *
     * @param anchor The name of the anchor.
     * @param location The location of the anchor as string.
     * @return True if the anchor was successfully saved.
     */
    public Try<Void> saveAnchorLocation(String anchor, String location) {
        Location parsed = locationManipulation.stringToLocation(location);
        return saveAnchorLocation(anchor, parsed);
    }

    /**
     * Saves an anchor.
     *
     * @param anchor The name of the anchor.
     * @param l The {@link Location} of the anchor.
     * @return True if the anchor was successfully saved.
     */
    public Try<Void> saveAnchorLocation(String anchor, Location l) {
        if (l == null) {
            return Try.failure(new IllegalArgumentException("Location cannot be null"));
        }
        getAnchorsConfigSection().set(anchor, locationManipulation.locationToString(l));
        anchors.put(anchor, l);
        return saveAnchors();
    }

    /**
     * Gets all anchors.
     *
     * @return An unmodifiable {@link Set} containing all anchors.
     */
    public Set<String> getAllAnchors() {
        return Collections.unmodifiableSet(anchors.keySet());
    }

    /**
     * Gets all anchors that the specified {@link Player} can access.
     *
     * @param player The {@link Player}.
     * @return An unmodifiable {@link Set} containing all anchors the specified {@link Player} can access.
     */
    public Set<String> getAnchors(@Nullable Player player) {
        if (player == null) {
            return anchors.keySet();
        } else {
            return getAnchorsForPlayer(player);
        }
    }

    private Set<String> getAnchorsForPlayer(@NotNull Player player) {
        return anchors.entrySet().stream()
                .filter(entry -> shouldIncludeAnchorForPlayer(entry.getKey(), entry.getValue(), player))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private boolean shouldIncludeAnchorForPlayer(String anchor, Location location, Player player) {
        var world = getLocationWorld(location);
        return world != null && playerCanAccess(player, world, anchor);
    }

    private @Nullable World getLocationWorld(@Nullable Location location) {
        if (location == null) {
            return null;
        }
        return location.getWorld();
    }

    private boolean playerCanAccess(Player player, World world, String anchor) {
        String worldPerm = "multiverse.access." + world.getName();
        if (playerCanAccess(player, worldPerm)) {
            return true;
        }
        Logging.finer(String.format("Not adding anchor %s to the list, user %s doesn't have the %s permission "
                + "and 'enforceaccess' is enabled!", anchor, player.getName(), worldPerm));
        return false;
    }

    private boolean playerCanAccess(Player player, String worldPerm) {
        return !config.getEnforceAccess() || player.hasPermission(worldPerm);
    }

    /**
     * Deletes the specified anchor.
     *
     * @param s The name of the anchor.
     * @return True if the anchor was successfully deleted.
     */
    public Try<Void> deleteAnchor(String s) {
        if (anchors.containsKey(s)) {
            anchors.remove(s);
            getAnchorsConfigSection().set(s, null);
            return saveAnchors();
        }
        return Try.failure(new IllegalArgumentException("Anchor does not exist"));
    }
}
