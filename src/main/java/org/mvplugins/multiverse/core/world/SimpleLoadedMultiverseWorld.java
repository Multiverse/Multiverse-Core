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
import org.bukkit.generator.BiomeProvider;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.api.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.api.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.api.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.config.NullLocation;
import org.mvplugins.multiverse.core.world.config.SpawnLocation;
import org.mvplugins.multiverse.core.world.config.WorldConfig;

public class SimpleLoadedMultiverseWorld extends SimpleMultiverseWorld implements LoadedMultiverseWorld {

    private final UUID worldUid;

    private final BlockSafety blockSafety;
    private final LocationManipulation locationManipulation;

    SimpleLoadedMultiverseWorld(
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
        BiomeProvider biomeProvider = world.getBiomeProvider();
        if (biomeProvider instanceof SingleBiomeProvider singleBiomeProvider) {
            worldConfig.setBiome(singleBiomeProvider.getBiome());
        }
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

    @Override
    public UUID getUID() {
        return worldUid;
    }

    @Override
    public Option<World> getBukkitWorld() {
        return Option.of(Bukkit.getWorld(worldUid));
    }

    @Override
    public Option<WorldType> getWorldType() {
        //noinspection deprecation
        return getBukkitWorld().map(World::getWorldType);
    }

    @Override
    public Option<Boolean> canGenerateStructures() {
        return getBukkitWorld().map(World::canGenerateStructures);
    }

    @Override
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
