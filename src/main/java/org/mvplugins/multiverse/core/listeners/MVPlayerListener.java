/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.listeners;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.DefaultEventPriority;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventMethod;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventPriorityKey;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.IgnoreIfCancelled;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.event.MVRespawnEvent;
import org.mvplugins.multiverse.core.locale.PluginLocales;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.teleportation.TeleportQueue;
import org.mvplugins.multiverse.core.utils.result.ResultChain;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.EntryFeeResult;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;
import org.mvplugins.multiverse.core.world.helpers.DimensionFinder;
import org.mvplugins.multiverse.core.world.helpers.EnforcementHandler;

/**
 * Multiverse's Listener for players.
 */
@Service
final class MVPlayerListener implements CoreListener {
    /**
     * How many locations to try when spreading a respawn around the world spawn, same as vanilla.
     */
    private static final int MAX_RESPAWN_RADIUS_CANDIDATES = 1024;

    /**
     * How many chunks around the spawn chunk may be loaded while looking for a respawn location. Candidates are
     * checked synchronously while the respawn event is handled, so candidates in other chunks are only used if
     * those chunks are already loaded.
     */
    private static final int RESPAWN_RADIUS_LOADABLE_CHUNK_RANGE = 1;

    private final Plugin plugin;
    private final CoreConfig config;
    private final Provider<WorldManager> worldManagerProvider;
    private final BlockSafety blockSafety;
    private final Server server;
    private final TeleportQueue teleportQueue;
    private final MVEconomist economist;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;
    private final Provider<MVCommandManager> commandManagerProvider;
    private final DestinationsProvider destinationsProvider;
    private final EnforcementHandler enforcementHandler;
    private final DimensionFinder dimensionFinder;
    private final CorePermissionsChecker corePermissionsChecker;
    private final AsyncSafetyTeleporter asyncSafetyTeleporter;

    @Inject
    MVPlayerListener(
            MultiverseCore plugin,
            CoreConfig config,
            Provider<WorldManager> worldManagerProvider,
            BlockSafety blockSafety,
            Server server,
            TeleportQueue teleportQueue,
            MVEconomist economist,
            WorldEntryCheckerProvider worldEntryCheckerProvider,
            Provider<MVCommandManager> commandManagerProvider,
            DestinationsProvider destinationsProvider,
            EnforcementHandler enforcementHandler,
            DimensionFinder dimensionFinder,
            CorePermissionsChecker corePermissionsChecker,
            AsyncSafetyTeleporter asyncSafetyTeleporter) {
        this.plugin = plugin;
        this.config = config;
        this.worldManagerProvider = worldManagerProvider;
        this.blockSafety = blockSafety;
        this.server = server;
        this.teleportQueue = teleportQueue;
        this.economist = economist;
        this.worldEntryCheckerProvider = worldEntryCheckerProvider;
        this.commandManagerProvider = commandManagerProvider;
        this.destinationsProvider = destinationsProvider;
        this.enforcementHandler = enforcementHandler;
        this.dimensionFinder = dimensionFinder;
        this.corePermissionsChecker = corePermissionsChecker;
        this.asyncSafetyTeleporter = asyncSafetyTeleporter;
    }

    private WorldManager getWorldManager() {
        return worldManagerProvider.get();
    }

    private MVCommandManager getCommandManager() {
        return commandManagerProvider.get();
    }

    private PluginLocales getLocales() {
        return getCommandManager().getLocales();
    }

    /**
     * This method is called when a player respawns.
     *
     * @param event The Event that was fired.
     */
    @EventMethod
    @EventPriorityKey("mvcore-player-respawn")
    @DefaultEventPriority(EventPriority.LOW)
    void playerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        LoadedMultiverseWorld mvWorld = getWorldManager().getLoadedWorld(player.getWorld()).getOrNull();
        if (mvWorld == null) {
            Logging.finer("Player '%s' died in a world that is not managed by Multiverse.", player.getName());
            return;
        }

        if (mvWorld.getBedRespawn() && event.isBedSpawn()) {
            Logging.fine("Spawning %s at their bed.", player.getName());
            return;
        }
        if (mvWorld.getAnchorRespawn() && event.isAnchorSpawn()) {
            Logging.fine("Spawning %s at their anchor.", player.getName());
            return;
        }

        getRespawnWorld(mvWorld)
                .onEmpty(() -> Logging.fine("No respawn-world determined for world '%s'.", mvWorld.getName()))
                .flatMap(respawnWorld -> {
                    Logging.finer("Using respawn-world '%s' for world '%s'.", respawnWorld.getName(), mvWorld.getName());
                    return getMostAccurateRespawnLocation(respawnWorld, event.getRespawnLocation())
                            .onEmpty(() -> Logging.finer("No accurate respawn-location determined for world '%s'.",
                                    mvWorld.getName()));
                })
                .peek(newRespawnLocation -> {
                    MVRespawnEvent respawnEvent = new MVRespawnEvent(newRespawnLocation, event.getPlayer());
                    this.server.getPluginManager().callEvent(respawnEvent);
                    if (respawnEvent.isCancelled()) {
                        Logging.fine("Player '%s' cancelled their respawn event.", player.getName());
                        return;
                    }
                    Logging.fine("Overriding respawn location for player '%s' to '%s'.", player.getName(), respawnEvent.getRespawnLocation());
                    event.setRespawnLocation(respawnEvent.getRespawnLocation());
                });
    }

    private Option<LoadedMultiverseWorld> getRespawnWorld(LoadedMultiverseWorld mvWorld) {
        if (!mvWorld.getRespawnWorldName().isEmpty()) {
            Logging.finer("Using configured respawn-world for world '%s'.", mvWorld.getName());
            return getWorldManager().getLoadedWorld(mvWorld.getRespawnWorldName())
                    .onEmpty(() -> {
                        Logging.warning("World '%s' has respawn-world property of '%s' that does not exist!",
                                mvWorld.getName(), mvWorld.getRespawnWorldName());
                    });
        } else if (!dimensionFinder.isOverworld(mvWorld) && config.getDefaultRespawnInOverworld()) {
            Logging.finer("Defaulting to overworld for world '%s'.", mvWorld.getName());
            return dimensionFinder.getOverworldWorld(mvWorld).flatMap(getWorldManager()::getLoadedWorld)
                    .onEmpty(() -> {
                        Logging.warning("World '%s' has no overworld to teleport to!",
                                mvWorld.getName());
                    });
        } else if (config.getDefaultRespawnWithinSameWorld()) {
            Logging.finer("Defaulting to same world for world '%s'.", mvWorld.getName());
            return Option.of(mvWorld);
        }
        return Option.none();
    }

    private Option<Location> getMostAccurateRespawnLocation(LoadedMultiverseWorld mvWorld, Location defaultRespawnLocation) {
        if (!config.getEnforceRespawnAtWorldSpawn() && Objects.equals(defaultRespawnLocation.getWorld(), mvWorld.getBukkitWorld().getOrNull())) {
            Logging.fine("Respawn location is within same world as respawn-world, not overriding.");
            return Option.none();
        }
        return Option.of(getSpawnLocationWithRespawnRadius(mvWorld, mvWorld.getSpawnLocation()));
    }

    /**
     * Spreads the respawn location around the world spawn like vanilla does. Multiverse would otherwise always
     * respawn players exactly at the configured spawn location, bypassing the respawn radius game rule.
     *
     * @param mvWorld       The world the player is respawning in.
     * @param spawnLocation The configured spawn location of that world.
     * @return A safe location within the respawn radius of the spawn, or the spawn location itself if none is found.
     */
    Location getSpawnLocationWithRespawnRadius(LoadedMultiverseWorld mvWorld, Location spawnLocation) {
        World world = spawnLocation.getWorld();
        if (world == null) {
            return spawnLocation;
        }
        int respawnRadius = getRespawnRadius(world);
        if (respawnRadius <= 0 || mvWorld.getGameMode() == GameMode.ADVENTURE) {
            Logging.finer("Not spreading respawn location for world '%s' with respawn radius %d.",
                    mvWorld.getName(), respawnRadius);
            return spawnLocation;
        }
        int spawnX = spawnLocation.getBlockX();
        int spawnZ = spawnLocation.getBlockZ();
        int radius = Math.max(1, Math.min(respawnRadius, getDistanceToBorder(world.getWorldBorder(), spawnX, spawnZ)));
        Logging.finer("Spreading respawn location for world '%s' with radius %d.", mvWorld.getName(), radius);

        // Go through the candidates in a random order without repeats, like vanilla does.
        int sideLength = radius * 2 + 1;
        long candidateCount = (long) sideLength * sideLength;
        long step = getCoprime(candidateCount);
        long offset = ThreadLocalRandom.current().nextLong(candidateCount);
        long attempts = Math.min(candidateCount, MAX_RESPAWN_RADIUS_CANDIDATES);
        for (long i = 0; i < attempts; i++) {
            long index = (offset + i * step) % candidateCount;
            int x = spawnX + (int) (index % sideLength) - radius;
            int z = spawnZ + (int) (index / sideLength) - radius;
            if (!canCheckRespawnCandidate(world, spawnX, spawnZ, x, z)) {
                continue;
            }
            Location candidate = getRespawnCandidate(world, spawnLocation, x, z);
            if (candidate != null) {
                Logging.fine("Using respawn location '%s' within radius %d of spawn for world '%s'.",
                        candidate, radius, mvWorld.getName());
                return candidate;
            }
        }
        Logging.finer("No safe respawn location found within radius %d of spawn for world '%s'.",
                radius, mvWorld.getName());
        return spawnLocation;
    }

    private boolean canCheckRespawnCandidate(World world, int spawnX, int spawnZ, int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        return (Math.abs(chunkX - (spawnX >> 4)) <= RESPAWN_RADIUS_LOADABLE_CHUNK_RANGE
                && Math.abs(chunkZ - (spawnZ >> 4)) <= RESPAWN_RADIUS_LOADABLE_CHUNK_RANGE)
                || world.isChunkLoaded(chunkX, chunkZ);
    }

    private @Nullable Location getRespawnCandidate(World world, Location spawnLocation, int x, int z) {
        if (world.hasCeiling()) {
            // The highest block would be the ceiling, so search up and down from the spawn height instead.
            Location location = new Location(world, x + 0.5, spawnLocation.getBlockY(), z + 0.5,
                    spawnLocation.getYaw(), spawnLocation.getPitch());
            Location safeLocation = blockSafety.findSafeSpawnLocation(location, 0,
                    config.getSafeLocationVerticalSearchRadius());
            return safeLocation != null && !isInLiquid(safeLocation) ? safeLocation : null;
        }
        int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING) + 1;
        if (y + 1 >= world.getMaxHeight()) {
            return null;
        }
        Location location = new Location(world, x + 0.5, y, z + 0.5,
                spawnLocation.getYaw(), spawnLocation.getPitch());
        return blockSafety.canSpawnAtLocationSafely(location) && !isInLiquid(location) ? location : null;
    }

    private boolean isInLiquid(Location location) {
        Block block = location.getBlock();
        return block.isLiquid() || block.getRelative(0, 1, 0).isLiquid();
    }

    private long getCoprime(long number) {
        long coprime = 17;
        while (gcd(coprime, number) != 1) {
            coprime++;
        }
        return coprime;
    }

    private long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private int getRespawnRadius(World world) {
        // Older servers only have the spawnRadius game rule, while newer ones renamed it to respawn_radius.
        GameRule<?> respawnRadiusRule = GameRule.getByName("respawn_radius");
        if (respawnRadiusRule == null) {
            respawnRadiusRule = GameRule.getByName("spawnRadius");
        }
        if (respawnRadiusRule == null) {
            return 0;
        }
        Object value = world.getGameRuleValue(respawnRadiusRule);
        return value instanceof Integer ? Math.max(0, (Integer) value) : 0;
    }

    private int getDistanceToBorder(WorldBorder worldBorder, int x, int z) {
        double halfSize = worldBorder.getSize() / 2.0;
        double minX = worldBorder.getCenter().getX() - halfSize;
        double maxX = worldBorder.getCenter().getX() + halfSize;
        double minZ = worldBorder.getCenter().getZ() - halfSize;
        double maxZ = worldBorder.getCenter().getZ() + halfSize;
        double distance = Math.min(
                Math.min(x - minX, maxX - x),
                Math.min(z - minZ, maxZ - z));
        return (int) Math.floor(distance);
    }

    @EventMethod
    // TODO: Consider if this key is needed anymore, need to remove from config.yml as well
    @EventPriorityKey("mvcore-player-spawn-location")
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            handleJoinLocation(player);
        } else {
            handleFirstSpawn(player);
        }
        handleGameModeAndFlight(player, player.getWorld());
    }

    private void handleFirstSpawn(Player player) {
        if (!config.getFirstSpawnOverride()) {
            Logging.finer("FirstSpawnOverride is disabled");
            // User has disabled the feature in config
            return;
        }
        Logging.fine("Moving NEW player to(firstspawnoverride): %s", config.getFirstSpawnLocation());
        destinationsProvider.parseDestination(config.getFirstSpawnLocation())
                .peek(destination -> teleportToDestinationOnJoin(player, destination))
                .onFailure(failure -> {
                    Logging.warning("Invalid destination in FirstSpawnLocation in config: %s");
                    Logging.warning(failure.getFailureMessage().formatted(getLocales()));
                });
    }

    private void handleJoinLocation(Player player) {
        if (!config.getEnableJoinDestination()) {
            Logging.finer("JoinDestination is disabled");
            // User has disabled the feature in config
            return;
        }
        if (config.getJoinDestination().isBlank()) {
            Logging.warning("Joindestination is enabled but no destination has been specified in config!");
            return;
        }
        if (corePermissionsChecker.hasJoinLocationBypassPermission(player)) {
            Logging.finer("Player %s has bypass permission for JoinDestination", player.getName());
            return;
        }
        Logging.finer("JoinDestination is " + config.getJoinDestination());
        destinationsProvider.parseDestination(config.getJoinDestination())
                .peek(destination -> teleportToDestinationOnJoin(player, destination))
                .onFailure(failure -> {
                    Logging.warning("Invalid destination in JoinDestination in config: %s");
                    Logging.warning(failure.getFailureMessage().formatted(getLocales()));
                });
    }

    private void teleportToDestinationOnJoin(Player player, DestinationInstance<?, ?> destination) {
        asyncSafetyTeleporter.to(destination).passengerMode(config.getPassengerMode()).teleportSingle(player)
                .onSuccess(result -> Logging.fine("Player %s has been teleported on join",
                        player.getName()))
                .onFailure(failure -> Logging.warning("Failed to teleport player %s on join: %s",
                        player.getName(), failure.get(0)));
    }

    /**
     * This method is called when a player changes worlds.
     * @param event The Event that was fired.
     */
    @EventMethod
    @EventPriorityKey("mvcore-player-changed-world")
    @DefaultEventPriority(EventPriority.NORMAL)
    void playerChangedWorld(PlayerChangedWorldEvent event) {
        this.handleGameModeAndFlight(event.getPlayer(), event.getPlayer().getWorld());
    }

    /**
     * This method is called when a player teleports anywhere.
     * @param event The Event that was fired.
     */
    @EventMethod
    @EventPriorityKey("mvcore-player-teleport")
    @DefaultEventPriority(EventPriority.HIGHEST)
    void playerTeleport(PlayerTeleportEvent event) {
        Player teleportee = event.getPlayer();

        if (event.isCancelled()) {
            Logging.finer("Teleport event for player '" + teleportee.getName() + "' was already cancelled.");
            return;
        }

        Logging.finer("Got teleport event for player '"
                + teleportee.getName() + "' with cause '" + event.getCause() + "'");

        Location fromLocation = event.getFrom();
        if (fromLocation == null || fromLocation.getWorld() == null) { // should never be null, but just in case
            Logging.finer("Teleport event for player '" + teleportee.getName() + "' has null from-location or world.");
            return;
        }

        Location toLocation = event.getTo();
        if (toLocation == null || toLocation.getWorld() == null) { // may be null on spigot
            Logging.finer("Teleport event for player '" + teleportee.getName() + "' has null to-location or world.");
            return;
        }

        Option<String> teleporterName = teleportQueue.popFromQueue(teleportee.getName());
        CommandSender teleporter = teleporterName.map(name -> {
            if (name.equalsIgnoreCase("CONSOLE")) {
                Logging.finer("We know the teleporter is the console! Magical!");
                return this.server.getConsoleSender();
            }
            return this.server.getPlayerExact(teleporterName.get());
        }).getOrNull();

        if (teleporter == null) {
            if (!config.getTeleportIntercept()) {
                Logging.finer("Teleport for %s was not initiated by multiverse and " +
                        "teleport intercept is disabled. Ignoring...", teleportee.getName());
                return;
            }
            Logging.finer("Unknown teleporter for teleport for %s. Using player as teleporter.", teleportee.getName());
            teleporter = teleportee;
        }

        Logging.finer("Teleporter %s is teleporting %s from %s to %s", teleporter.getName(), teleportee.getName(),
                fromLocation, toLocation);

        MultiverseWorld fromWorld = getWorldManager().getLoadedWorld(fromLocation.getWorld()).getOrNull();
        LoadedMultiverseWorld toWorld = getWorldManager().getLoadedWorld(toLocation.getWorld()).getOrNull();
        if (toWorld == null) {
            Logging.fine("Player '" + teleportee.getName() + "' is teleporting to world '"
                    + toLocation.getWorld().getName() + "' which is not managed by Multiverse-Core.  No further "
                    + "actions will be taken by Multiverse-Core.");
            return;
        }
        if (fromLocation.getWorld().equals(toLocation.getWorld())) {
            // The player is Teleporting to the same world.
            Logging.finer("Player '" + teleportee.getName() + "' is teleporting to the same world.");
            this.stateSuccess(teleportee.getName(), toWorld.getName());
            return;
        }

        CommandSender finalTeleporter = teleporter;
        ResultChain entryResult = worldEntryCheckerProvider.forSender(finalTeleporter).canEnterWorld(fromWorld, toWorld)
                .onSuccessReason(EntryFeeResult.Success.class, reason -> {
                    if (reason == EntryFeeResult.Success.ENOUGH_MONEY) {
                        economist.payEntryFee((Player) finalTeleporter, toWorld);
                    }
                })
                .onFailure(results -> {
                    event.setCancelled(true);
                    getCommandManager().getCommandIssuer(finalTeleporter).sendError(results.getLastResultMessage());
                });

        Logging.fine("Teleport result: %s", entryResult);
    }

    private void stateSuccess(String playerName, String worldName) {
        Logging.fine("MV-Core is allowing Player '" + playerName
                + "' to go to '" + worldName + "'.");
    }

    /**
     * This method is called to adjust the portal location to the actual portal location (and not
     * right outside of it.
     * @param event The Event that was fired.
     */
    @EventMethod
    @DefaultEventPriority(EventPriority.LOWEST)
    @IgnoreIfCancelled
    void playerPortalCheck(PlayerPortalEvent event) {
        Location fromLocation = event.getFrom();
        if (fromLocation == null || fromLocation.getWorld() == null) {
            Logging.warning("PlayerPortalEvent's from world is null!");
            return;
        }
        if (fromLocation.getWorld().getBlockAt(fromLocation).getType() == Material.NETHER_PORTAL) {
            return;
        }

        // Player was actually outside of the portal, adjust the from location
        Location newLocation = blockSafety.findPortalBlockNextTo(fromLocation);
        if (newLocation != null) {
            event.setFrom(newLocation);
        }
    }

    /**
     * This method is called when a player actually portals via a vanilla style portal.
     * @param event The Event that was fired.
     */
    @EventMethod
    @EventPriorityKey("mvcore-player-portal")
    @DefaultEventPriority(EventPriority.HIGH)
    @IgnoreIfCancelled
    void playerPortal(PlayerPortalEvent event) {
        Location fromLocation = event.getFrom();
        if (fromLocation == null || fromLocation.getWorld() == null) { // should never be null, but just in case
            Logging.finer("PlayerPortalEvent's from world is null!");
            return;
        }

        Location toLocation = event.getTo();
        if (toLocation == null || toLocation.getWorld() == null) { // may be null on spigot
            Logging.finer("PlayerPortalEvent's to world is null!");
            return;
        }
        if (config.isUsingCustomPortalSearch()) {
            event.setSearchRadius(config.getCustomPortalSearchRadius());
        }
        if (Objects.equals(fromLocation.getWorld(), toLocation.getWorld())) {
            // The player is Portaling to the same world.
            Logging.finer("Player '" + event.getPlayer().getName() + "' is portaling to the same world.");
            return;
        }

        MultiverseWorld fromWorld = getWorldManager().getLoadedWorld(fromLocation.getWorld()).getOrNull();
        LoadedMultiverseWorld toWorld = getWorldManager().getLoadedWorld(toLocation.getWorld()).getOrNull();
        if (toWorld == null) {
            Logging.fine("Player '" + event.getPlayer().getName() + "' is portaling to world '"
                    + toLocation.getWorld().getName() + "' which is not managed by Multiverse-Core.  No further "
                    + "actions will be taken by Multiverse-Core.");
            return;
        }
        ResultChain entryResult = worldEntryCheckerProvider.forSender(event.getPlayer()).canEnterWorld(fromWorld, toWorld)
                .onFailure(results -> {
                    event.setCancelled(true);
                    getCommandManager().getCommandIssuer(event.getPlayer()).sendError(results.getLastResultMessage());
                });

        Logging.fine("Teleport result: %s", entryResult);
    }

    /**
     * Handles the gamemode for the specified {@link Player}. Delays the enforcement if configured to do so
     * to ensure multiverse has final say over other plugins changing gamemodes.
     *
     * @param player The {@link Player}.
     * @param world  The {@link World} the player is supposed to be in.
     */
    private void handleGameModeAndFlight(final Player player, World world) {
        if (config.getGamemodeAndFlightEnforceDelay() <= 0) {
            doGameModeAndFlightEnforcement(player, world);
            return;
        }
        server.getScheduler().runTaskLater(
                this.plugin,
                () -> doGameModeAndFlightEnforcement(player, world),
                config.getGamemodeAndFlightEnforceDelay()
        );
    }

    private void doGameModeAndFlightEnforcement(Player player, World world) {
        if (!player.isOnline() || !player.getWorld().equals(world)) {
            Logging.finer("Player %s is no longer online or not in the expected world '%s'", player.getName(), world.getName());
            return;
        }
        Logging.finer("Handling gamemode and flight for player %s in world '%s'", player.getName(), world.getName());
        enforcementHandler.handleFlightEnforcement(player);
        enforcementHandler.handleGameModeEnforcement(player);
    }
}
