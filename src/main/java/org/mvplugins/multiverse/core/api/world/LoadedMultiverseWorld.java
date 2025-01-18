package org.mvplugins.multiverse.core.api.world;

import io.vavr.control.Option;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Extension of {@link MultiverseWorld} that represents a world that is currently loaded with bukkit world object.
 *
 * @since 5.0
 */
public interface LoadedMultiverseWorld extends MultiverseWorld {
    /**
     * Gets the Unique ID of this world.
     *
     * @return Unique ID of this world.
     * @since 5.0
     */
    UUID getUID();

    /**
     * Gets the Bukkit world object that this world describes.
     *
     * @return Bukkit world object.
     * @since 5.0
     */
    Option<World> getBukkitWorld();

    /**
     * Gets the type of this world.
     *
     * @return Type of this world.
     * @since 5.0
     */
    Option<WorldType> getWorldType();

    /**
     * Gets whether or not structures are being generated.
     *
     * @return True if structures are being generated.
     * @since 5.0
     */
    Option<Boolean> canGenerateStructures();

    /**
     * Get a list of all players in this World.
     *
     * @return A list of all Players currently residing in this world
     * @since 5.0
     */
    Option<List<Player>> getPlayers();
}
