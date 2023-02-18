/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import co.aikar.commands.BukkitCommandIssuer;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.action.ActionResponse;
import com.onarandombox.MultiverseCore.api.action.ActionResult;
import com.onarandombox.MultiverseCore.event.MVRespawnEvent;
import com.onarandombox.MultiverseCore.utils.player.PlayerActionChecker;
import com.onarandombox.MultiverseCore.utils.player.checkresult.BlacklistResult;
import com.onarandombox.MultiverseCore.utils.player.checkresult.EntryFeeResult;
import com.onarandombox.MultiverseCore.utils.player.checkresult.NullPlaceResult;
import com.onarandombox.MultiverseCore.utils.player.checkresult.PlayerLimitResult;
import com.onarandombox.MultiverseCore.utils.player.checkresult.WorldAccessResult;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Multiverse's {@link Listener} for players.
 */
public class MVPlayerListener implements Listener {
    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;
    private final PlayerActionChecker actionChecker;

    private final Map<String, String> playerWorld = new ConcurrentHashMap<>();

    public MVPlayerListener(MultiverseCore plugin) {
        this.plugin = plugin;
        worldManager = plugin.getMVWorldManager();
        actionChecker = plugin.getPlayerActionChecker();
    }

    /**
     * @return the playerWorld-map
     */
    @Deprecated
    public Map<String, String> getPlayerWorld() {
        return playerWorld;
    }

    /**
     * This method is called when a player respawns.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void playerRespawn(PlayerRespawnEvent event) {
        World world = event.getPlayer().getWorld();
        MVWorld mvWorld = this.worldManager.getMVWorld(world.getName());
        // If it's not a World MV manages we stop.
        if (mvWorld == null) {
            return;
        }

        if (mvWorld.getBedRespawn() && (event.isBedSpawn() || event.isAnchorSpawn())) {
            Logging.fine("Spawning %s at their %s.", event.getPlayer().getName(), event.isBedSpawn() ? "BED" : "ANCHOR");
            return;
        }

        // Get the instance of the World the player should respawn at.
        MVWorld respawnWorld = null;
        if (this.worldManager.isMVWorld(mvWorld.getRespawnToWorld())) {
            respawnWorld = this.worldManager.getMVWorld(mvWorld.getRespawnToWorld());
        }

        // If it's null then it either means the World doesn't exist or the value is blank, so we don't handle it.
        // NOW: We'll always handle it to get more accurate spawns
        if (respawnWorld != null) {
            world = respawnWorld.getCBWorld();
        }
        // World has been set to the appropriate world
        Location respawnLocation = getMostAccurateRespawnLocation(world);

        MVRespawnEvent respawnEvent = new MVRespawnEvent(respawnLocation, event.getPlayer(), "compatability");
        this.plugin.getServer().getPluginManager().callEvent(respawnEvent);
        event.setRespawnLocation(respawnEvent.getPlayersRespawnLocation());
    }

    private Location getMostAccurateRespawnLocation(World w) {
        MVWorld mvw = this.worldManager.getMVWorld(w.getName());
        if (mvw != null) {
            return mvw.getSpawnLocation();
        }
        return w.getSpawnLocation();
    }

    /**
     * This method is called when a player joins the server.
     * @param event The Event that was fired.
     */
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            Logging.finer("Player joined for the FIRST time!");
            if (!plugin.getMVConfig().getFirstSpawnOverride()) {
                return;
            }
            Logging.fine("Moving NEW player to(firstspawnoverride): " + worldManager.getFirstSpawnWorld().getSpawnLocation());
            this.sendPlayerToDefaultWorld(player);
            return;
        }

        Logging.finer("Player joined AGAIN!");
        MVWorld world = worldManager.getMVWorld(player.getWorld());
        if (world == null) {
            Logging.fine("Player is in a world that is not managed by Multiverse. Ignoring.");
            return;
        }

        if (!this.plugin.getPlayerActionChecker().hasAccessToWorld(player, world).isSuccessful()) {
            player.sendMessage("[MV] - Sorry you can't be in this world anymore!");
            this.sendPlayerToDefaultWorld(player);
        }

        // Handle the Players GameMode setting for the new world.
        this.handleGameModeAndFlight(event.getPlayer(), world);
        playerWorld.put(player.getName(), player.getWorld().getName()); //TODO REMOVE
    }

    /**
     * This method is called when a player changes worlds.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerChangedWorld(PlayerChangedWorldEvent event) {
        MVWorld world = worldManager.getMVWorld(event.getPlayer().getWorld());
        if (world == null) {
            Logging.fine("Player is in a world that is not managed by Multiverse. Ignoring.");
            return;
        }

        this.handleGameModeAndFlight(event.getPlayer(), world);
        playerWorld.put(event.getPlayer().getName(), event.getPlayer().getWorld().getName()); //TODO REMOVE
    }

    /**
     * This method is called when a player teleports anywhere.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled() || event.getTo() == null) {
            return;
        }

        Logging.fine("Got teleport event for player '"
                + event.getPlayer().getName() + "' with cause '" + event.getCause() + "'");

        Player teleportee = event.getPlayer();
        CommandSender teleporter = null;

        //TODO - Redo teleporter queue handling
        String teleporterName = MultiverseCore.getPlayerTeleporter(teleportee.getName());
        if (teleporterName == null && !this.plugin.getMVConfig().getTeleportIntercept()) {
            Logging.finer("Teleporting without intercepting.");
            return;
        }

        if (Objects.equals(teleporterName, "CONSOLE")) {
            Logging.finer("We know the teleporter is the console! Magical!");
            teleporter = this.plugin.getServer().getConsoleSender();
        } else {
            teleporter = this.plugin.getServer().getPlayerExact(teleporterName);
        }
        Logging.fine("Inferred sender '" + teleporter + "' from name '"
                + teleporterName + "', fetched from name '" + teleportee.getName() + "'");

        if (teleporter == null) {
            Logging.finer("We don't know who the teleporter is, so we'll assume it's the player.");
            teleporter = teleportee;
        }

        MVWorld fromWorld = this.worldManager.getMVWorld(event.getFrom().getWorld());
        MVWorld toWorld = this.worldManager.getMVWorld(event.getTo().getWorld());
        ActionResponse actionCheckResponse = this.actionChecker.canGoFromToWorld(teleporter, teleportee, fromWorld, toWorld);
        Logging.fine(actionCheckResponse.toString());
        if (!actionCheckResponse.isSuccessful()) {
            tellReason(teleporter, teleportee, fromWorld, toWorld, actionCheckResponse);
            event.setCancelled(true);
            return;
        }

        if (actionCheckResponse.hasResult(EntryFeeResult.ENOUGH_MONEY)) {
            double price = toWorld.getPrice();
            Material currency = toWorld.getCurrency();
            this.plugin.getEconomist().payEntryFee((Player) teleporter, price, currency);
        }

        Logging.fine("MV-Core is allowing Player '" + teleportee.getName() + "' to go to '" + toWorld.getName() + "'.");
    }

    /**
     * This method is called to adjust the portal location to the actual portal location (and not
     * right outside of it.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerPortalCheck(PlayerPortalEvent event) {
        if (event.isCancelled() || event.getFrom() == null) {
            return;
        }

        // REMEMBER! getTo MAY be NULL HERE!!!
        // If the player was actually outside of the portal, adjust the from location
        if (event.getFrom().getWorld().getBlockAt(event.getFrom()).getType() != Material.NETHER_PORTAL) {
            Location newloc = this.plugin.getSafeTTeleporter().findPortalBlockNextTo(event.getFrom());
            // TODO: Fix this. Currently, we only check for PORTAL blocks. I'll have to figure out what
            // TODO: we want to do here.
            if (newloc != null) {
                event.setFrom(newloc);
            }
        }
        // Wait for the adjust, then return!
        if (event.getTo() == null) {
            return;
        }
    }

    /**
     * This method is called when a player actually portals via a vanilla style portal.
     * @param event The Event that was fired.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void playerPortal(PlayerPortalEvent event) {
        if (event.isCancelled() || event.getFrom() == null || event.getTo() == null) {
            return;
        }

        MVWorld fromWorld = this.worldManager.getMVWorld(event.getFrom().getWorld());
        MVWorld toWorld = this.worldManager.getMVWorld(event.getTo().getWorld());
        ActionResponse actionCheckResponse = this.actionChecker.canGoFromToWorld(event.getPlayer(), event.getPlayer(), fromWorld, toWorld);
        Logging.fine(actionCheckResponse.toString());
        if (!actionCheckResponse.isSuccessful()) {
            tellReason(event.getPlayer(), event.getPlayer(), fromWorld, toWorld, actionCheckResponse);
            event.setCancelled(true);
            return;
        }

        if (!this.plugin.getMVConfig().isUsingDefaultPortalSearch()) {
            event.setSearchRadius(this.plugin.getMVConfig().getPortalSearchRadius());
        }
    }

    private void sendPlayerToDefaultWorld(@NotNull Player player) {
        // Remove the player 1 tick after the login. I'm sure there's GOT to be a better way to do this...
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin,
                () -> player.teleport(plugin.getMVWorldManager().getFirstSpawnWorld().getSpawnLocation()),
                1L);
    }

    /**
     * Handles the gamemode for the specified {@link Player}.
     * @param player The {@link Player}.
     * @param world The world the player is in.
     */
    private void handleGameModeAndFlight(@NotNull Player player, @NotNull MVWorld world) {
        // We perform this task one tick later to MAKE SURE that the player actually reaches the
        // destination world, otherwise we'd be changing the player mode if they havent moved anywhere.
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                this.plugin,
                ()-> applyGameModeAndFlight(player, world),
                1L);
    }

    /**
     * Applies the gamemode and flight for the specified {@link Player}.
     *
     * @param player The {@link Player}.
     * @param world The world the player is in.
     */
    public void applyGameModeAndFlight(@NotNull Player player, @NotNull MVWorld world) {
        if (MVPlayerListener.this.actionChecker.canKeepGameMode(player, world).isSuccessful()) {
            Logging.fine("Player: " + player.getName() + " is IMMUNE to gamemode changes!");
            return;
        }

        // Check that the player is in the new world and they haven't been teleported elsewhere or the event cancelled.
        if (player.getWorld() != world.getCBWorld()) {
            Logging.fine("The gamemode/allowfly was NOT changed for player '%s' because he is now in world '%s' instead of world '%s'",
                    player.getName(), player.getWorld().getName(), world.getName());
            return;
        }
        Logging.fine("Handling gamemode for player: %s, Changing to %s", player.getName(), world.getGameMode().toString());
        Logging.finest("From World: %s", player.getWorld());
        Logging.finest("To World: %s", world);
        player.setGameMode(world.getGameMode());

        // Check if their flight mode should change
        // TODO need a override permission for this
        if (player.getAllowFlight() && !world.getAllowFlight() && player.getGameMode() != GameMode.CREATIVE) {
            player.setAllowFlight(false);
            if (player.isFlying()) {
                player.setFlying(false);
            }
        } else if (world.getAllowFlight()) {
            if (player.getGameMode() == GameMode.CREATIVE) {
                player.setAllowFlight(true);
            }
        }
    }

    /**
     * Sends the reason for the teleportation failure to the sender.
     *
     * @param sender        The sender of the command.
     * @param teleportee    The player being teleported.
     * @param fromWorld     The world the player is teleporting from.
     * @param toWorld       The world the player is teleporting to.
     * @param result        The result of the teleportation.
     */
    private void tellReason(@NotNull CommandSender sender,
                            @NotNull Player teleportee,
                            @Nullable MVWorld fromWorld,
                            @Nullable MVWorld toWorld,
                            @NotNull ActionResult result
    ) {
        BukkitCommandIssuer issuer = this.plugin.getMVCommandManager().getCommandIssuer(sender);
        String targetName = issuer.getIssuer() == teleportee ? "You" : teleportee.getName();

        if (result.hasResult(NullPlaceResult.NULL_DESTINATION)) {
            issuer.sendMessage(targetName + " cannot be teleported to because the destination is null.");
            return;
        }
        if (result.hasResult(NullPlaceResult.NULL_LOCATION)) {
            issuer.sendMessage(targetName + " cannot be teleported to because the location is null.");
            return;
        }
        if (result.hasResult(NullPlaceResult.NULL_WORLD)) {
            issuer.sendMessage(targetName + " cannot be teleported because the world is null.");
            return;
        }

        if (result.hasResult(WorldAccessResult.NO_WORLD_ACCESS)) {
            issuer.sendMessage(targetName + " cannot be teleported to because you does not have access to " + toWorld.getName());
        }
        if (result.hasResult(PlayerLimitResult.EXCEED_PLAYERLIMIT)) {
            issuer.sendMessage(targetName + " cannot cannot enter " + toWorld.getName() + " because it is full.");
        }
        if (result.hasResult(EntryFeeResult.NOT_ENOUGH_MONEY)) {
            issuer.sendMessage("You do not have enough money to pay for " + targetName + " to enter " + toWorld.getName());
            issuer.sendMessage("The entry fee required is " + this.plugin.getEconomist().formatPrice(toWorld));
        }
        if (result.hasResult(EntryFeeResult.CANNOT_PAY_ENTRY_FEE)) {
            issuer.sendMessage("You do not have the ability to pay the entry fee for " + targetName + " to enter " + toWorld.getName());
        }
        if (result.hasResult(BlacklistResult.BLACKLISTED)) {
            if (toWorld.equals(fromWorld)) {
                issuer.sendMessage(targetName + " cannot teleport within " + toWorld.getName() + " because it is blacklisted.");
            } else {
                issuer.sendMessage(fromWorld.getName() + " is blacklisted from teleporting to " + toWorld.getName());
            }
        }
    }
}
