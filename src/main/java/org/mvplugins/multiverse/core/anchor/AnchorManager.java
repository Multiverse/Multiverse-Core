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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.api.LocationManipulation;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;

/**
 * Manages anchors.
 */
@Service
public class AnchorManager {
    private static final String ANCHORS_SECTION_NAME = "anchors";
    private static final String ANCHORS_FILE_NAME = "anchors.yml";

    private final Map<String, Location> anchors;
    private final File anchorsFile;
    private final FileConfiguration anchorConfig;
    private final LocationManipulation locationManipulation;
    private final WorldManager worldManager;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;

    @Inject
    AnchorManager(
            @NotNull MultiverseCore plugin,
            @NotNull LocationManipulation locationManipulation,
            @NotNull WorldManager worldManager,
            @NotNull WorldEntryCheckerProvider worldEntryCheckerProvider) {
        this.anchors = new HashMap<>();
        this.anchorsFile = new File(plugin.getDataFolder(), ANCHORS_FILE_NAME);
        this.anchorConfig = new YamlConfiguration();
        this.locationManipulation = locationManipulation;
        this.worldManager = worldManager;
        this.worldEntryCheckerProvider = worldEntryCheckerProvider;
    }

    /**
     * Loads all anchors.
     *
     * @return Empty {@link Try} if all anchors were successfully loaded, or the exception if an error occurred.
     */
    public Try<Void> loadAnchors() {
        return Try.run(() -> {
            anchors.clear();
            anchorConfig.load(anchorsFile);
            ConfigurationSection anchorsSection = getAnchorConfigSection();
            Set<String> anchorKeys = anchorsSection.getKeys(false);
            anchorKeys.forEach(key -> {
                //world:x,y,z:pitch:yaw
                String locationString = anchorsSection.getString(key, "");
                Location anchorLocation = this.locationManipulation.stringToLocation(locationString);
                if (anchorLocation == null) {
                    Logging.warning("The location for anchor '%s' is INVALID.", key);
                    return;
                }
                this.anchors.put(key, anchorLocation);
            });
        });
    }

    /**
     * Saves all anchors.
     *
     * @return True if all anchors were successfully saved.
     */
    public Try<Void> saveAnchors() {
        return Try.run(() -> anchorConfig.save(anchorsFile));
    }

    /**
     * Gets the {@link Location} associated with an anchor.
     *
     * @param anchorName    The name of the anchor.
     * @return The {@link Location}.
     */
    public Location getAnchorLocation(@Nullable String anchorName) {
        return this.anchors.getOrDefault(anchorName, null);
    }

    /**
     * Saves an anchor.
     *
     * @param anchorName        The name of the anchor.
     * @param locationString    The location of the anchor as string.
     * @return Empty {@link Try} if all anchors were successfully loaded, or the exception if an error occurred.
     */
    public Try<Location> saveAnchorLocation(String anchorName, String locationString) {
        Location parsed = this.locationManipulation.stringToLocation(locationString);
        if (parsed == null) {
            return Try.failure(new IOException("Invalid location string: " + locationString));
        }
        return this.saveAnchorLocation(anchorName, parsed).map(ignore -> getAnchorLocation(anchorName));
    }

    /**
     * Saves an anchor.
     *
     * @param anchorName    The name of the anchor.
     * @param location      The {@link Location} of the anchor.
     * @return Empty {@link Try} if all anchors were successfully loaded, or the exception if an error occurred.
     */
    public Try<Void> saveAnchorLocation(@NotNull String anchorName, @NotNull Location location) {
        getAnchorConfigSection().set(anchorName, this.locationManipulation.locationToString(location));
        this.anchors.put(anchorName, location);
        return saveAnchors();
    }

    /**
     * Gets all anchors.
     *
     * @return An unmodifiable {@link Set} containing all anchors.
     */
    public Set<String> getAllAnchors() {
        return Collections.unmodifiableSet(this.anchors.keySet());
    }

    /**
     * Gets all anchors that the specified {@link Player} can access.
     *
     * @param player    The {@link Player}.
     * @return An unmodifiable {@link Set} containing all anchors the specified {@link Player} can access.
     */
    public Set<String> getAnchors(@Nullable Player player) {
        if (player == null) {
            return getAllAnchors();
        }

        Set<String> myAnchors = new HashSet<>();

        this.anchors.forEach((anchorName, anchorLocation) -> {
            World anchorWorld = anchorLocation.getWorld();
            if (anchorWorld == null) {
                return;
            }

            worldManager.getWorld(anchorWorld)
                    .map(world -> worldEntryCheckerProvider.forSender(player).canAccessWorld(world).isSuccess())
                    .peek(success -> {
                        if (success) {
                            myAnchors.add(anchorName);
                        } else {
                            Logging.finer("Player '%s' cannot access anchor '%s'.",
                                    player.getName(), anchorName);
                        }
                    })
                    .onEmpty(() -> {
                        Logging.finer("Anchor '%s' located in world '%s is not in a Multiverse world.",
                                anchorName, anchorLocation.getWorld().getName());
                    });
        });

        return myAnchors;
    }

    /**
     * Deletes the specified anchor.
     *
     * @param anchorName    The name of the anchor.
     * @return True if the anchor was successfully deleted.
     */
    public Try<Void> deleteAnchor(String anchorName) {
        if (this.anchors.containsKey(anchorName)) {
            this.anchors.remove(anchorName);
            getAnchorConfigSection().set(anchorName, null);
            return this.saveAnchors();
        }
        return Try.failure(new Exception("Anchor " + anchorName + " not found."));
    }

    private ConfigurationSection getAnchorConfigSection() {
        ConfigurationSection anchorsSection = anchorConfig.getConfigurationSection(ANCHORS_SECTION_NAME);
        return anchorsSection == null
                ? anchorConfig.createSection(ANCHORS_SECTION_NAME)
                : anchorsSection;
    }
}
