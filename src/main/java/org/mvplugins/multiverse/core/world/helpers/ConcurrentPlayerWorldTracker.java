package org.mvplugins.multiverse.core.world.helpers;

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
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.MultiverseCore;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.AvailableSince("5.4")
@Service
public final class ConcurrentPlayerWorldTracker implements Listener {

    private final Map<String, String> playerWorldMap;

    @Inject
    ConcurrentPlayerWorldTracker(@NotNull MultiverseCore plugin) {
        this.playerWorldMap = new ConcurrentHashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @ApiStatus.AvailableSince("5.4")
    @NotNull
    public List<String> getOnlinePlayers() {
        return List.copyOf(playerWorldMap.keySet());
    }

    @ApiStatus.AvailableSince("5.4")
    @Nullable
    public String getPlayerWorld(String playerName) {
        return playerWorldMap.get(playerName);
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
