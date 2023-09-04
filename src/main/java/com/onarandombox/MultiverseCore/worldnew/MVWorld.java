package com.onarandombox.MultiverseCore.worldnew;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.BlockSafety;
import com.onarandombox.MultiverseCore.api.LocationManipulation;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.worldnew.config.NullLocation;
import com.onarandombox.MultiverseCore.worldnew.config.SpawnLocation;
import com.onarandombox.MultiverseCore.worldnew.config.WorldConfig;
import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MVWorld extends OfflineWorld {
    private static final int SPAWN_LOCATION_SEARCH_TOLERANCE = 16;
    private static final int SPAWN_LOCATION_SEARCH_RADIUS = 16;

    private final UUID worldUid;

    private final BlockSafety blockSafety;
    private final SafeTTeleporter safeTTeleporter;
    private final LocationManipulation locationManipulation;

    MVWorld(
            @NotNull World world,
            @NotNull WorldConfig worldConfig,
            @NotNull BlockSafety blockSafety,
            @NotNull SafeTTeleporter safeTTeleporter,
            @NotNull LocationManipulation locationManipulation
    ) {
        super(world.getName(), worldConfig);
        this.worldUid = world.getUID();
        this.blockSafety = blockSafety;
        this.safeTTeleporter = safeTTeleporter;
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
            world.setSpawnLocation(newLocation.getBlockX(), newLocation.getBlockY(), newLocation.getBlockZ());
        }
        worldConfig.getSpawnLocation().setWorld(world);
    }

    private Location readSpawnFromWorld(World world) { // TODO: Refactor... this is copy pasted and bad
        Location location = world.getSpawnLocation();
        // Set the worldspawn to our configspawn
        // Verify that location was safe
        if (!blockSafety.playerCanSpawnHereSafely(location)) {
            if (!this.getAdjustSpawn()) {
                Logging.fine("Spawn location from world.dat file was unsafe!!");
                Logging.fine("NOT adjusting spawn for '" + this.getAlias() + "' because you told me not to.");
                Logging.fine("To turn on spawn adjustment for this world simply type:");
                Logging.fine("/mvm set adjustspawn true " + this.getAlias());
                return location;
            }
            // If it's not, find a better one.
            Logging.warning("Spawn location from world.dat file was unsafe. Adjusting...");
            Logging.warning("Original Location: " + locationManipulation.strCoordsRaw(location));
            Location newSpawn = safeTTeleporter.getSafeLocation(location,
                    SPAWN_LOCATION_SEARCH_TOLERANCE, SPAWN_LOCATION_SEARCH_RADIUS);
            // I think we could also do this, as I think this is what Notch does.
            // Not sure how it will work in the nether...
            //Location newSpawn = this.spawnLocation.getWorld().getHighestBlockAt(this.spawnLocation).getLocation();
            if (newSpawn != null) {
                Logging.info("New Spawn for '%s' is located at: %s",
                        this.getName(), locationManipulation.locationToString(newSpawn));
                return newSpawn;
            } else {
                // If it's a standard end world, let's check in a better place:
                Location newerSpawn;
                newerSpawn = blockSafety.getTopBlock(new Location(world, 0, 0, 0));
                if (newerSpawn != null) {
                    Logging.info("New Spawn for '%s' is located at: %s",
                            this.getName(), locationManipulation.locationToString(newerSpawn));
                    return newerSpawn;
                } else {
                    Logging.severe("Safe spawn NOT found!!!");
                }
            }
        }
        return location;
    }

    public Option<World> getBukkitWorld() {
        return Option.of(Bukkit.getWorld(worldUid));
    }

    public Option<WorldType> getWorldType() {
        return getBukkitWorld().map(World::getWorldType);
    }

    public Option<Boolean> canGenerateStructures() {
        return getBukkitWorld().map(World::canGenerateStructures);
    }

    @Override
    void setWorldConfig(WorldConfig worldConfig) {
        super.setWorldConfig(worldConfig);
        setupWorldConfig(getBukkitWorld().get());
    }

    @Override
    public String toString() {
        return "MVWorld{" +
                "name='" + worldName + "', " +
                "env='" + getEnvironment() + "', " +
                "type='" + getWorldType().getOrNull() + "', " +
                "gen='" + getGenerator() + "'" +
                '}';
    }
}
