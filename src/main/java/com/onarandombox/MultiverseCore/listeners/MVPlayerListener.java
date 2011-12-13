/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.listeners;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.event.MVRespawnEvent;
import com.onarandombox.MultiverseCore.utils.PermissionTools;
import com.onarandombox.MultiverseCore.utils.SafeTTeleporter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

import java.util.logging.Level;

public class MVPlayerListener extends PlayerListener {
    private MultiverseCore plugin;
    private SafeTTeleporter mvteleporter;
    private MVWorldManager worldManager;
    private PermissionTools pt;


    public MVPlayerListener(MultiverseCore plugin) {
        this.plugin = plugin;
        worldManager = plugin.getMVWorldManager();
        pt = new PermissionTools(plugin);
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        // Check whether the Server is set to prefix the chat with the World name.
        // If not we do nothing, if so we need to check if the World has an Alias.
        if (MultiverseCore.PrefixChat) {
            String world = event.getPlayer().getWorld().getName();
            String prefix = "";
            // If we're not a MV world, don't do anything
            if (!this.worldManager.isMVWorld(world)) {
                return;
            }
            MultiverseWorld mvworld = this.worldManager.getMVWorld(world);
            if (mvworld.isHidden()) {
                return;
            }
            prefix = mvworld.getColoredWorldString();
            String format = event.getFormat();
            event.setFormat("[" + prefix + "]" + format);
        }
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {


        World world = event.getPlayer().getWorld();
        MultiverseWorld mvWorld = this.worldManager.getMVWorld(world.getName());
        // If it's not a World MV manages we stop.
        if (mvWorld == null) {
            return;
        }


        if (mvWorld.getBedRespawn() && event.isBedSpawn()) {
            this.plugin.log(Level.FINE, "Spawning " + event.getPlayer().getName() + " at their bed");
            return;
        }

        // Get the instance of the World the player should respawn at.
        MultiverseWorld respawnWorld = null;
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
        MultiverseWorld mvw = this.worldManager.getMVWorld(w.getName());
        if (mvw != null) {
            return mvw.getSpawnLocation();
        }
        return w.getSpawnLocation();
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (!p.hasPlayedBefore()) {
            this.plugin.log(Level.WARNING, "Player joined first!");
            this.plugin.log(Level.WARNING, "Loc: " + worldManager.getFirstSpawnWorld().getSpawnLocation());
            // This will override other spawn plugins atm :(
            this.spawnNewPlayer(p);
            return;
        } else {
            this.plugin.log(Level.WARNING, "Player joined AGAIN!");
        }
        // Handle the Players GameMode setting for the new world.
        if (MultiverseCore.EnforceGameModes) {
            this.handleGameMode(event.getPlayer(), event.getPlayer().getWorld());
        }
    }

    @Override
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        // Handle the Players GameMode setting for the new world.
        if (MultiverseCore.EnforceGameModes) {
            // Not yet implemented, but eventually we'll switch to this!
            //if (this.plugin.getMVWorldManager().getMVWorld(event.getPlayer().getWorld()).getEnforceGameMode())
            this.handleGameMode(event.getPlayer(), event.getPlayer().getWorld());
        }
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.removePlayerSession(event.getPlayer());
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player teleportee = event.getPlayer();
        Player teleporter = null;
        String teleporterName = MultiverseCore.getPlayerTeleporter(teleportee.getName());
        if (teleporterName != null) {
            teleporter = this.plugin.getServer().getPlayer(teleporterName);
        }
        MultiverseWorld fromWorld = this.worldManager.getMVWorld(event.getFrom().getWorld().getName());
        MultiverseWorld toWorld = this.worldManager.getMVWorld(event.getTo().getWorld().getName());
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            // The player is Teleporting to the same world.
            this.plugin.log(Level.FINER, "Player '" + teleportee.getName() + "' is teleporting to the same world.");
            return;
        }
        // TODO: Refactor these lines.
        // Charge the teleporter
        event.setCancelled(!pt.playerHasMoneyToEnter(fromWorld, toWorld, teleporter, teleportee, true));
        if (event.isCancelled() && teleporter != null) {
            this.plugin.log(Level.FINE, "Player '" + teleportee.getName() + "' was DENIED ACCESS to '" + event.getTo().getWorld().getName() +
                    "' because '" + teleporter.getName() + "' don't have the FUNDS required to enter it.");
            return;
        }
        if (MultiverseCore.EnforceAccess) {
            event.setCancelled(!pt.playerCanGoFromTo(fromWorld, toWorld, teleporter, teleportee));
            if (event.isCancelled() && teleporter != null) {
                this.plugin.log(Level.FINE, "Player '" + teleportee.getName() + "' was DENIED ACCESS to '" + event.getTo().getWorld().getName() +
                        "' because '" + teleporter.getName() + "' don't have: multiverse.access." + event.getTo().getWorld().getName());
            }
        } else {
            this.plugin.log(Level.FINE, "Player '" + teleportee.getName() + "' was allowed to go to '" + event.getTo().getWorld().getName() + "' because enforceaccess is off.");
        }
    }

    @Override
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.isCancelled() || event.getFrom() == null) {
            return;
        }
        // REMEMBER! getTo MAY be NULL HERE!!!
        // If the player was actually outside of the portal, adjust the from location
        if (event.getFrom().getWorld().getBlockAt(event.getFrom()).getType() != Material.PORTAL) {
            Location newloc = SafeTTeleporter.findPortalBlockNextTo(event.getFrom());
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
        MultiverseWorld fromWorld = this.worldManager.getMVWorld(event.getFrom().getWorld().getName());
        MultiverseWorld toWorld = this.worldManager.getMVWorld(event.getTo().getWorld().getName());
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            // The player is Portaling to the same world.
            this.plugin.log(Level.FINER, "Player '" + event.getPlayer().getName() + "' is portaling to the same world.");
            return;
        }
        event.setCancelled(!pt.playerHasMoneyToEnter(fromWorld, toWorld, event.getPlayer(), event.getPlayer(), true));
        if (event.isCancelled()) {
            this.plugin.log(Level.FINE, "Player '" + event.getPlayer().getName() + "' was DENIED ACCESS to '" + event.getTo().getWorld().getName() +
                    "' because they don't have the FUNDS required to enter.");
            return;
        }
        if (MultiverseCore.EnforceAccess) {
            event.setCancelled(!pt.playerCanGoFromTo(fromWorld, toWorld, event.getPlayer(), event.getPlayer()));
            if (event.isCancelled()) {
                this.plugin.log(Level.FINE, "Player '" + event.getPlayer().getName() + "' was DENIED ACCESS to '" + event.getTo().getWorld().getName() +
                        "' because they don't have: multiverse.access." + event.getTo().getWorld().getName());
            }
        } else {
            this.plugin.log(Level.FINE, "Player '" + event.getPlayer().getName() + "' was allowed to go to '" + event.getTo().getWorld().getName() + "' because enforceaccess is off.");
        }
    }

    private void spawnNewPlayer(Player player) {
        // Spawn the player 1 tick after the login. I'm sure there's GOT to be a better way to do this...
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new SpawnNewbie(player, this.plugin.getMVWorldManager().getFirstSpawnWorld().getSpawnLocation()), 1L);
    }
    // FOLLOWING 2 Methods and Private class handle Per Player GameModes.
    private void handleGameMode(Player player, World world) {
        this.plugin.log(Level.FINE, "Handeling gamemode for player: " + player.getName());
        MultiverseWorld mvWorld = this.worldManager.getMVWorld(world.getName());
        if (mvWorld != null) {
            this.handleGameMode(player, mvWorld);
        }
    }

    public void handleGameMode(Player player, MultiverseWorld world) {
        // We perform this task one tick later to MAKE SURE that the player actually reaches the
        // destination world, otherwise we'd be changing the player mode if they havent moved anywhere.
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new HandleGameMode(player, world), 1L);
    }

    private class SpawnNewbie implements Runnable {
        private Player player;
        private Location spawn;

        private SpawnNewbie(Player player, Location spawn) {
            this.player = player;
            this.spawn = spawn;
        }
        @Override
        public void run() {
            this.player.teleport(this.spawn);
        }
    }

    /**
     * The following private class is used to handle player game mode changes within a scheduler.
     */
    private class HandleGameMode implements Runnable {

        private Player player;
        private MultiverseWorld world;

        private HandleGameMode(Player player, MultiverseWorld world) {
            this.player = player;
            this.world = world;
        }

        @Override
        public void run() {
            // Check that the player is in the new world and they haven't been teleported elsewhere or the event cancelled.
            if (player.getWorld().getName().equals(world.getCBWorld().getName())) {
                MultiverseCore.staticLog(Level.FINE, "Handeling gamemode for player: " + player.getName() + ", " + world.getGameMode().toString());
                MultiverseCore.staticLog(Level.FINE, "PWorld: " + player.getWorld());
                MultiverseCore.staticLog(Level.FINE, "AWorld: " + world);
                player.setGameMode(world.getGameMode());
            }
        }
    }
}
