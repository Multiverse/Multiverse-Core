package org.mvplugins.multiverse.core.world.helpers;

import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.MultiverseCore;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which players are in which worlds, using a thread-safe map.
 * This allows async access to online players list and the world they are in.
 */
@ApiStatus.AvailableSince("5.4")
@Service
public final class ConcurrentPlayerWorldTracker implements Listener {

    private final Map<String, String> playerWorldMap;

    @Inject
    ConcurrentPlayerWorldTracker(@NotNull MultiverseCore plugin) {
        this.playerWorldMap = new ConcurrentHashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Get an unmodifiable collection of all online player names on the server.
     *
     * @return Unmodifiable collection of online player names.
     */
    @ApiStatus.AvailableSince("5.4")
    @NotNull
    @UnmodifiableView
    public Collection<String> getOnlinePlayers() {
        return Collections.unmodifiableCollection(playerWorldMap.keySet());
    }

    /**
     * Get the world name a player is currently in.
     *
     * @param playerName Name of the player.
     * @return World name the player is in, or null if the player is not online.
     */
    @ApiStatus.AvailableSince("5.4")
    @NotNull
    public Option<String> getPlayerWorld(String playerName) {
        return Option.of(playerWorldMap.get(playerName));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        setPlayerWorld(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerChangedWorld(@NotNull PlayerChangedWorldEvent event) {
        setPlayerWorld(event.getPlayer());
    }

    private void setPlayerWorld(Player player) {
        String playerName = player.getName();
        String worldName = player.getWorld().getName();
        playerWorldMap.put(playerName, worldName);
    }

    @EventHandler
    private void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        playerWorldMap.remove(playerName);
    }
}
