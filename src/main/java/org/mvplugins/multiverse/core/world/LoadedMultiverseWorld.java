package org.mvplugins.multiverse.core.world;

import java.util.List;
import java.util.UUID;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.world.location.NullLocation;
import org.mvplugins.multiverse.core.world.location.SpawnLocation;

/**
 * Extension of {@link MultiverseWorld} that represents a world that is currently loaded with bukkit world object.
 */
public final class LoadedMultiverseWorld extends MultiverseWorld {

    private final UUID worldUid;

    private final BlockSafety blockSafety;
    private final LocationManipulation locationManipulation;

    LoadedMultiverseWorld(
            @NotNull World world,
            @NotNull WorldConfig worldConfig,
            @NotNull BlockSafety blockSafety,
            @NotNull LocationManipulation locationManipulation) {
        super(world.getName(), worldConfig);
        this.worldUid = world.getUID();
        this.blockSafety = blockSafety;
        this.locationManipulation = locationManipulation;

        setupWorldConfig(world);
        setupSpawnLocation(world);
    }

    private void setupWorldConfig(World world) {
        worldConfig.setMVWorld(this);
        worldConfig.load();
        worldConfig.setEnvironment(world.getEnvironment());
        worldConfig.setSeed(world.getSeed());
    }

    private void setupSpawnLocation(World world) {
        Location spawnLocation = worldConfig.getSpawnLocation();
        if (spawnLocation == null || spawnLocation instanceof NullLocation) {
            SpawnLocation newLocation = new SpawnLocation(readSpawnFromWorld(world));
            worldConfig.setSpawnLocation(newLocation);
        }
    }

    private Location readSpawnFromWorld(World world) {
        Location location = world.getSpawnLocation();

        // Verify that location was safe
        if (blockSafety.canSpawnAtLocationSafely(location)) {
            return location;
        }

        if (!this.getAdjustSpawn()) {
            Logging.fine("Spawn location from world.dat file was unsafe!!");
            Logging.fine("NOT adjusting spawn for '" + this.getAlias() + "' because you told me not to.");
            Logging.fine("To turn on spawn adjustment for this world simply type:");
            Logging.fine("/mvm set adjustspawn true " + this.getAlias());
            return location;
        }

        // The location is not safe, so we need to find a better one.
        Logging.warning("Spawn location from world.dat file was unsafe. Adjusting...");
        Logging.warning("Original Location: " + locationManipulation.strCoordsRaw(location));
        Location newSpawn = blockSafety.findSafeSpawnLocation(location);
        // I think we could also do this, as I think this is what Notch does.
        // Not sure how it will work in the nether...
        //Location newSpawn = this.spawnLocation.getWorld().getHighestBlockAt(this.spawnLocation).getLocation();
        if (newSpawn != null) {
            Logging.info("New Spawn for '%s' is located at: %s",
                    this.getName(), locationManipulation.locationToString(newSpawn));
            return newSpawn;
        }

        // If it's a standard end world, let's check in a better place:
        Logging.fine("Checking for a safe location using top block...");
        Location newerSpawn;
        newerSpawn = blockSafety.getTopBlock(new Location(world, 0, 0, 0));
        if (newerSpawn != null) {
            Logging.info("New Spawn for '%s' is located at: %s",
                    this.getName(), locationManipulation.locationToString(newerSpawn));
            return newerSpawn;
        }

        Logging.severe("Safe spawn NOT found!!!");
        return location;
    }

    /**
     * Gets the Unique ID of this world.
     *
     * @return Unique ID of this world.
     */
    public UUID getUID() {
        return worldUid;
    }

    /**
     * Gets the Bukkit world object that this world describes.
     *
     * @return Bukkit world object.
     */
    public Option<World> getBukkitWorld() {
        return Option.of(Bukkit.getWorld(worldUid));
    }

    /**
     * Gets the type of this world.
     *
     * @return Type of this world.
     */
    public Option<WorldType> getWorldType() {
        //noinspection deprecation
        return getBukkitWorld().map(World::getWorldType);
    }

    /**
     * Gets whether or not structures are being generated.
     *
     * @return True if structures are being generated.
     */
    public Option<Boolean> canGenerateStructures() {
        return getBukkitWorld().map(World::canGenerateStructures);
    }

    /**
     * Get a list of all players in this World.
     *
     * @return A list of all Players currently residing in this world
     */
    public Option<List<Player>> getPlayers() {
        return getBukkitWorld().map(World::getPlayers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setWorldConfig(WorldConfig worldConfig) {
        super.setWorldConfig(worldConfig);
        setupWorldConfig(getBukkitWorld().get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "LoadedMultiverseWorld{"
                + "name='" + worldName + "', "
                + "env='" + getEnvironment() + "', "
                + "type='" + getWorldType().getOrNull() + "', "
                + "gen='" + getGenerator() + "'"
                + '}';
    }
}
